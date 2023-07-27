package com.kaii.dentix.domain.contents.dao;

import com.kaii.dentix.domain.contents.domain.ContentsToCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentsListRepository extends JpaRepository<ContentsToCategory, Long> {

    List<ContentsToCategory> findByContentsId(int contentsId);

}
