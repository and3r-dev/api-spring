package br.com.project.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.project.api.model.ProcessModel;

public interface ProcessRepository extends JpaRepository<ProcessModel, Integer> {
}
