package com.kaii.dentix.domain.contents.dao;

import com.kaii.dentix.domain.contents.domain.ContentsCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentsCategoryRepository extends JpaRepository<ContentsCategory, Long> {

    List<ContentsCategory> findAll();

}
