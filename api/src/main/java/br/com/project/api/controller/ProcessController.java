package br.com.project.api.controller;

import br.com.project.api.model.ProcessModel;
import br.com.project.api.repository.ProcessRepository;
import br.com.project.api.FileStorageProperties;
import br.com.project.api.dto.ProcessDTOVisualizar;
import br.com.project.api.dto.ProcessDTOIndex;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class ProcessController {

    @Autowired
    private ProcessRepository acao;

    private final Path fileStorageLocation;

    private static final String BASE_URL = "http://localhost:8000/uploads/";

    @Autowired
    public ProcessController(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
    }

    @PostMapping(value = "/processo/cadastrar", consumes = "multipart/form-data")
    public ResponseEntity<ProcessModel> cadastrar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("npu") String npu,
            @RequestParam("municipio") String municipio,
            @RequestParam("uf") String uf,
            @RequestParam(value = "data_cadastro", required = false) String dataCadastro,
            @RequestParam(value = "data_visualizacao", required = false) String dataVisualizacao) {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            Path npuDirectory = this.fileStorageLocation.resolve(String.valueOf(npu)).normalize();
            Files.createDirectories(npuDirectory);

            Path targetLocation = npuDirectory.resolve(fileName);
            file.transferTo(targetLocation);

            ProcessModel process = new ProcessModel();
            process.setNpu(npu);
            process.setMunicipio(municipio);
            process.setUf(uf);
            process.setUpload_documento(fileName);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            if (dataCadastro != null) {
                LocalDateTime dataCadastroParsed = LocalDateTime.parse(dataCadastro, formatter);
                process.setData_cadastro(dataCadastroParsed);
            }
            if (dataVisualizacao != null) {
                LocalDateTime dataVisualizacaoParsed = LocalDateTime.parse(dataVisualizacao, formatter);
                process.setData_visualizacao(dataVisualizacaoParsed);
            }

            ProcessModel savedProcess = acao.save(process);
            return ResponseEntity.ok(savedProcess);

        } catch (IOException ex) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/")
    public PagedModel<EntityModel<ProcessDTOIndex>> selecionar(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProcessModel> processPage = acao.findAll(pageable);

        List<EntityModel<ProcessDTOIndex>> ProcessDTOIndexs = processPage.stream()
                .map(process -> new ProcessDTOIndex(
                        process.getId(),
                        process.getNpu(),
                        process.getData_cadastro(),
                        process.getMunicipio(),
                        process.getUf()))
                .map(EntityModel::of)
                .collect(Collectors.toList());

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                processPage.getSize(),
                processPage.getNumber(),
                processPage.getTotalElements(),
                processPage.getTotalPages());

        PagedModel<EntityModel<ProcessDTOIndex>> pagedModel = PagedModel.of(ProcessDTOIndexs, metadata);

        pagedModel.add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProcessController.class).selecionar(page
                        - 1, size))
                        .withRel("previous").expand());
        pagedModel.add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProcessController.class).selecionar(page
                        + 1, size))
                        .withRel("next").expand());

        return pagedModel;
    }

    @PutMapping(value = "/processo/editar", consumes = "multipart/form-data")
    public ResponseEntity<ProcessModel> editar(
            @RequestParam("id") int id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("npu") String npu,
            @RequestParam("municipio") String municipio,
            @RequestParam("uf") String uf,
            @RequestParam(value = "data_cadastro", required = false) String dataCadastro,
            @RequestParam(value = "data_visualizacao", required = false) String dataVisualizacao) {

        try {
            ProcessModel existingProcess = acao.findById(id).orElse(null);
            if (existingProcess == null) {
                return ResponseEntity.notFound().build();
            }

            existingProcess.setNpu(npu);
            existingProcess.setMunicipio(municipio);
            existingProcess.setUf(uf);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            if (dataCadastro != null) {
                LocalDateTime dataCadastroParsed = LocalDateTime.parse(dataCadastro, formatter);
                existingProcess.setData_cadastro(dataCadastroParsed);
            }
            if (dataVisualizacao != null) {
                LocalDateTime dataVisualizacaoParsed = LocalDateTime.parse(dataVisualizacao, formatter);
                existingProcess.setData_visualizacao(dataVisualizacaoParsed);
            }

            if (file != null && !file.isEmpty()) {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());

                Path npuDirectory = this.fileStorageLocation.resolve(String.valueOf(npu)).normalize();
                Files.createDirectories(npuDirectory);

                if (!fileName.equals(existingProcess.getUpload_documento())) {
                    Path existingFilePath = npuDirectory.resolve(existingProcess.getUpload_documento());
                    Files.deleteIfExists(existingFilePath);
                }

                Path targetLocation = npuDirectory.resolve(fileName);
                file.transferTo(targetLocation);

                existingProcess.setUpload_documento(fileName);
            }

            ProcessModel updatedProcess = acao.save(existingProcess);
            return ResponseEntity.ok(updatedProcess);

        } catch (IOException ex) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/processo/deletar/{id}")
    public ResponseEntity<Boolean> deletar(@PathVariable("id") int id) {
        try {
            ProcessModel existingProcess = acao.findById(id).orElse(null);
            if (existingProcess == null) {
                return ResponseEntity.ok(false);
            }

            Path npuDirectory = this.fileStorageLocation.resolve(String.valueOf(existingProcess.getNpu())).normalize();
            if (Files.exists(npuDirectory)) {
                Files.walk(npuDirectory)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }

            acao.delete(existingProcess);
            return ResponseEntity.ok(true);

        } catch (IOException ex) {
            return ResponseEntity.status(500).body(false);
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(false);
        }
    }

    @GetMapping("/processo/{id}")
    public EntityModel<ProcessDTOVisualizar> visualizar(@PathVariable int id) {
        Optional<ProcessModel> optionalProcess = acao.findById(id);

        if (optionalProcess.isPresent()) {
            ProcessModel process = optionalProcess.get();

            process.setData_visualizacao(LocalDateTime.now());
            acao.save(process);

            String fileUrl = BASE_URL + process.getNpu() + "/" + process.getUpload_documento();

            ProcessDTOVisualizar processDTO = new ProcessDTOVisualizar(
                    process.getId(),
                    process.getNpu(),
                    process.getData_cadastro(),
                    process.getUf(),
                    fileUrl);

            EntityModel<ProcessDTOVisualizar> entityModel = EntityModel.of(processDTO);
            entityModel.add(
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProcessController.class).visualizar(id))
                            .withSelfRel());
            return entityModel;
        } else {
            throw new RuntimeException("Processo não encontrado com o ID: " + id);
        }
    }

}
