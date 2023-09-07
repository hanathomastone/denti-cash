package com.kaii.dentix.domain.contents.dao;

import com.kaii.dentix.domain.contents.domain.Contents;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentsRepository extends JpaRepository<Contents, Long> {

    List<Contents> findAll(Sort sort);
}
