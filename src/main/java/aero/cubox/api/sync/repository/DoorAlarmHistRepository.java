package aero.cubox.api.sync.repository;

import aero.cubox.api.domain.entity.Dept;
import aero.cubox.api.domain.entity.DoorAlarmHist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoorAlarmHistRepository extends JpaRepository<DoorAlarmHist, Integer> {



}
