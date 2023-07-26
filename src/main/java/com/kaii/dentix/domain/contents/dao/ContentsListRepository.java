package com.kaii.dentix.domain.contents.dao;

import com.kaii.dentix.domain.contents.domain.ContentsList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentsListRepository extends JpaRepository<ContentsList, Long> {

    List<ContentsList> findByContentsId(Long contentsId);

}
