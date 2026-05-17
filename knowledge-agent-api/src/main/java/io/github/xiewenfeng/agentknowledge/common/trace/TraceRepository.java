package io.github.xiewenfeng.agentknowledge.common.trace;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TraceRepository extends JpaRepository<TraceEntity, String> {
}
