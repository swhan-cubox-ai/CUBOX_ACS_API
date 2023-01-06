package aero.cubox.api.sync.service;

import aero.cubox.api.common.mapper.TerminalMapper;
import aero.cubox.api.domain.entity.DoorAlarmHist;
import aero.cubox.api.service.AbstractService;
import aero.cubox.api.sync.repository.DoorAlarmHistRepository;
import aero.cubox.api.sync.vo.DoorAlarmVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;


@Service
@Slf4j
public class DoorAlarmHistService extends AbstractService<DoorAlarmHist, Integer> {


    @Autowired
    private DoorAlarmHistRepository repository;

    @Autowired
    private TerminalMapper mapper;


    @Override
    protected JpaRepository<DoorAlarmHist, Integer> getRepository() { return repository; }

    public void insertDoorAlarmHist(DoorAlarmVO doorAlarmVO){

        DoorAlarmHist terminalInfo = mapper.getTerminalInfo(doorAlarmVO.getTerminalCd());

        DoorAlarmHist doorAlarmHist =
                DoorAlarmHist.builder()
                        .evtDt(Timestamp.valueOf(doorAlarmVO.getEvtDt()))
                        .doorAlarmTyp(doorAlarmVO.getDoorAlarmTyp())
                        .terminalId(terminalInfo.getTerminalId())
                        .terminalCd(doorAlarmVO.getTerminalCd())
                        .terminalTyp(terminalInfo.getTerminalTyp())
                        .modelNm(terminalInfo.getModelNm())
                        .mgmtNum(terminalInfo.getMgmtNum())
                        .ipAddr(terminalInfo.getIpAddr())
                        .complexAuthTyp(terminalInfo.getComplexAuthTyp())
                        .doorId(terminalInfo.getDoorId())
                        .doorNm(terminalInfo.getDoorNm())
                        .buildingId(terminalInfo.getBuildingId())
                        .buildingNm(terminalInfo.getBuildingNm())
                        .createdAt(new Timestamp(new Date().getTime()))
                        .updatedAt(new Timestamp(new Date().getTime()))
                        .build();

        repository.save(doorAlarmHist);

    };

}
