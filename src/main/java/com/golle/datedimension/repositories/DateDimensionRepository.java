package com.golle.datedimension.repositories;

import com.golle.datedimension.beans.DateDimension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface DateDimensionRepository extends JpaRepository<DateDimension, String> {

}
