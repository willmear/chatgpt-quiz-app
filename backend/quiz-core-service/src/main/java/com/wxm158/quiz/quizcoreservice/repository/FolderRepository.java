package com.wxm158.quiz.quizcoreservice.repository;

import com.wxm158.quiz.quizcoreservice.model.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long> {
}
