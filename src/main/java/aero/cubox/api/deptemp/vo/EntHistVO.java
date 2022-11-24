package aero.cubox.api.deptemp.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import java.sql.Timestamp;

public class EntHistVO {

    private Integer id;

    private Timestamp evtDt;

    private String entEvtTyp;

    private String terminalCd;

    private String empCd;

    private String empNm;

    private Integer faceId;

    private String cardNo;

    private String cardClassTyp;

    private String cardStateTyp;

    private String cardTagTyp;

    private Timestamp begDt;

    private Timestamp endDt;

    private String authWayTyp;

    private Float matchScore;

    private Float faceThreshold;

    private Timestamp captureAt;

    private Timestamp tagAt;

    private Float tagCardNo;

    private Float tagEmpCd;

    private Float temper;

    private Float maskConfidence;

    private String terminalTyp;

    private String doorCd;

    private String doorNm;

    private String buildingCd;

    private String buildingNm;

    private String deptCd;

    private String deptNm;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private String entFaceImg;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getEvtDt() {
        return evtDt;
    }

    public void setEvtDt(Timestamp evtDt) {
        this.evtDt = evtDt;
    }

    public String getEntEvtTyp() {
        return entEvtTyp;
    }

    public void setEntEvtTyp(String entEvtTyp) {
        this.entEvtTyp = entEvtTyp;
    }

    public String getTerminalCd() {
        return terminalCd;
    }

    public void setTerminalCd(String terminalCd) {
        this.terminalCd = terminalCd;
    }

    public String getEmpCd() {
        return empCd;
    }

    public void setEmpCd(String empCd) {
        this.empCd = empCd;
    }

    public String getEmpNm() {
        return empNm;
    }

    public void setEmpNm(String empNm) {
        this.empNm = empNm;
    }

    public Integer getFaceId() {
        return faceId;
    }

    public void setFaceId(Integer faceId) {
        this.faceId = faceId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardClassTyp() {
        return cardClassTyp;
    }

    public void setCardClassTyp(String cardClassTyp) {
        this.cardClassTyp = cardClassTyp;
    }

    public String getCardStateTyp() {
        return cardStateTyp;
    }

    public void setCardStateTyp(String cardStateTyp) {
        this.cardStateTyp = cardStateTyp;
    }

    public String getCardTagTyp() {
        return cardTagTyp;
    }

    public void setCardTagTyp(String cardTagTyp) {
        this.cardTagTyp = cardTagTyp;
    }

    public Timestamp getBegDt() {
        return begDt;
    }

    public void setBegDt(Timestamp begDt) {
        this.begDt = begDt;
    }

    public Timestamp getEndDt() {
        return endDt;
    }

    public void setEndDt(Timestamp endDt) {
        this.endDt = endDt;
    }

    public String getAuthWayTyp() {
        return authWayTyp;
    }

    public void setAuthWayTyp(String authWayTyp) {
        this.authWayTyp = authWayTyp;
    }

    public Float getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Float matchScore) {
        this.matchScore = matchScore;
    }

    public Float getFaceThreshold() {
        return faceThreshold;
    }

    public void setFaceThreshold(Float faceThreshold) {
        this.faceThreshold = faceThreshold;
    }

    public Timestamp getCaptureAt() {
        return captureAt;
    }

    public void setCaptureAt(Timestamp captureAt) {
        this.captureAt = captureAt;
    }

    public Timestamp getTagAt() {
        return tagAt;
    }

    public void setTagAt(Timestamp tagAt) {
        this.tagAt = tagAt;
    }

    public Float getTagCardNo() {
        return tagCardNo;
    }

    public void setTagCardNo(Float tagCardNo) {
        this.tagCardNo = tagCardNo;
    }

    public Float getTagEmpCd() {
        return tagEmpCd;
    }

    public void setTagEmpCd(Float tagEmpCd) {
        this.tagEmpCd = tagEmpCd;
    }

    public Float getTemper() {
        return temper;
    }

    public void setTemper(Float temper) {
        this.temper = temper;
    }

    public Float getMaskConfidence() {
        return maskConfidence;
    }

    public void setMaskConfidence(Float maskConfidence) {
        this.maskConfidence = maskConfidence;
    }

    public String getTerminalTyp() {
        return terminalTyp;
    }

    public void setTerminalTyp(String terminalTyp) {
        this.terminalTyp = terminalTyp;
    }

    public String getDoorCd() {
        return doorCd;
    }

    public void setDoorCd(String doorCd) {
        this.doorCd = doorCd;
    }

    public String getDoorNm() {
        return doorNm;
    }

    public void setDoorNm(String doorNm) {
        this.doorNm = doorNm;
    }

    public String getBuildingCd() {
        return buildingCd;
    }

    public void setBuildingCd(String buildingCd) {
        this.buildingCd = buildingCd;
    }

    public String getBuildingNm() {
        return buildingNm;
    }

    public void setBuildingNm(String buildingNm) {
        this.buildingNm = buildingNm;
    }

    public String getDeptCd() {
        return deptCd;
    }

    public void setDeptCd(String deptCd) {
        this.deptCd = deptCd;
    }

    public String getDeptNm() {
        return deptNm;
    }

    public void setDeptNm(String deptNm) {
        this.deptNm = deptNm;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEntFaceImg() {
        return entFaceImg;
    }

    public void setEntFaceImg(String entFaceImg) {
        this.entFaceImg = entFaceImg;
    }
}
