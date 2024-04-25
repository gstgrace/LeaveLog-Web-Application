package com.lms.repository;

import com.lms.models.LeaveDetails;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom repository for managing leave operations that are not directly supported by
 * the Spring Data JPA interface. This class uses the Criteria API to dynamically
 * build SQL queries for fetching leave records based on specific criteria.
 */
@Repository
public class LeaveManageNativeSqlRepo {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Retrieves all leave records matching a specific status.
   *
   * @param status the status of leaves to filter by; expected values are "PENDING", "ACCEPTED", or "REJECTED".
   * @return a list of LeaveDetails entities that match the given status
   */
  public List<LeaveDetails> getAllLeavesOnStatus(String status) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<LeaveDetails> cq = cb.createQuery(LeaveDetails.class);
    Root<LeaveDetails> leaveDetails = cq.from(LeaveDetails.class);
    List<Predicate> predicates = new ArrayList<>();

    // Add condition based on the status input
    if ("PENDING".equals(status)) {
      predicates.add(cb.equal(leaveDetails.get("status"), "PENDING"));
    } else if ("ACCEPTED".equals(status)) {
      predicates.add(cb.equal(leaveDetails.get("status"), "APPROVED"));
    } else if ("REJECTED".equals(status)) {
      predicates.add(cb.equal(leaveDetails.get("status"), "REJECTED"));
    }

    // Apply the predicates to the criteria query
    cq.where(predicates.toArray(new Predicate[0]));

    return entityManager.createQuery(cq).getResultList();
  }

}
