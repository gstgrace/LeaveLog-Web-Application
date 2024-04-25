package com.lms.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lms.models.LeaveType;

/**
 * Repository interface for performing CRUD operations on the LeaveType entities.
 * Extends JpaRepository to provide standard data access functionalities with additional
 * capabilities for querying leave types based on specific attributes.
 */
@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Integer> {

  /**
   * Retrieves a LeaveType by its type name.
   *
   * @param typeName the name of the leave type to find
   * @return an Optional containing the LeaveType if found, or an empty Optional if no leave type matches the given name
   */
  Optional<LeaveType> findByTypeName(String typeName);

}
