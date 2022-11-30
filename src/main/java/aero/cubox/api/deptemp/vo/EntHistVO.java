package aero.cubox.api.deptemp.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.annotation.processing.Generated;
import javax.persistence.Column;
import java.sql.Timestamp;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EntHistVO {

    private Integer id;

    private String evtDt;

    private String entEvtTyp;

    private String terminalCd;

    private String empCd;

    private String empNm;

    private Integer faceId;

    private String cardNo;

    private String cardClassTyp;

    private String cardStateTyp;

    private String cardTagTyp;

    private String begDt;

    private String endDt;

    private String authWayTyp;

    private Float matchScore;

    private Float faceThreshold;

    private String captureAt;

    private String tagAt;

    private String tagCardNo;

    private String tagEmpCd;

    private Double temper;

    private Float maskConfidence;

    private String terminalTyp;

    private String doorCd;

    private String doorNm;

    private String buildingCd;

    private String buildingNm;

    private String deptCd;

    private String deptNm;

    private String belongNm;

    private String createdAt;

    private String updatedAt;

    private String entFaceImg;

//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public String getEvtDt() {
//        return evtDt;
//    }
//
//    public void setEvtDt(String evtDt) {
//        this.evtDt = evtDt;
//    }
//
//    public String getEntEvtTyp() {
//        return entEvtTyp;
//    }
//
//    public void setEntEvtTyp(String entEvtTyp) {
//        this.entEvtTyp = entEvtTyp;
//    }
//
//    public String getTerminalCd() {
//        return terminalCd;
//    }
//
//    public void setTerminalCd(String terminalCd) {
//        this.terminalCd = terminalCd;
//    }
//
//    public String getEmpCd() {
//        return empCd;
//    }
//
//    public void setEmpCd(String empCd) {
//        this.empCd = empCd;
//    }
//
//    public String getEmpNm() {
//        return empNm;
//    }
//
//    public void setEmpNm(String empNm) {
//        this.empNm = empNm;
//    }
//
//    public Integer getFaceId() {
//        return faceId;
//    }
//
//    public void setFaceId(Integer faceId) {
//        this.faceId = faceId;
//    }
//
//    public String getCardNo() {
//        return cardNo;
//    }
//
//    public void setCardNo(String cardNo) {
//        this.cardNo = cardNo;
//    }
//
//    public String getCardClassTyp() {
//        return cardClassTyp;
//    }
//
//    public void setCardClassTyp(String cardClassTyp) {
//        this.cardClassTyp = cardClassTyp;
//    }
//
//    public String getCardStateTyp() {
//        return cardStateTyp;
//    }
//
//    public void setCardStateTyp(String cardStateTyp) {
//        this.cardStateTyp = cardStateTyp;
//    }
//
//    public String getCardTagTyp() {
//        return cardTagTyp;
//    }
//
//    public void setCardTagTyp(String cardTagTyp) {
//        this.cardTagTyp = cardTagTyp;
//    }
//
//    public String getBegDt() {
//        return begDt;
//    }
//
//    public void setBegDt(String begDt) {
//        this.begDt = begDt;
//    }
//
//    public String getEndDt() {
//        return endDt;
//    }
//
//    public void setEndDt(String endDt) {
//        this.endDt = endDt;
//    }
//
//    public String getAuthWayTyp() {
//        return authWayTyp;
//    }
//
//    public void setAuthWayTyp(String authWayTyp) {
//        this.authWayTyp = authWayTyp;
//    }
//
//    public Float getMatchScore() {
//        return matchScore;
//    }
//
//    public void setMatchScore(Float matchScore) {
//        this.matchScore = matchScore;
//    }
//
//    public Float getFaceThreshold() {
//        return faceThreshold;
//    }
//
//    public void setFaceThreshold(Float faceThreshold) {
//        this.faceThreshold = faceThreshold;
//    }
//
//    public String getCaptureAt() {
//        return captureAt;
//    }
//
//    public void setCaptureAt(String captureAt) {
//        this.captureAt = captureAt;
//    }
//
//    public String getTagAt() {
//        return tagAt;
//    }
//
//    public void setTagAt(String tagAt) {
//        this.tagAt = tagAt;
//    }
//
//    public String getTagCardNo() {
//        return tagCardNo;
//    }
//
//    public void setTagCardNo(String tagCardNo) {
//        this.tagCardNo = tagCardNo;
//    }
//
//    public String getTagEmpCd() {
//        return tagEmpCd;
//    }
//
//    public void setTagEmpCd(String tagEmpCd) {
//        this.tagEmpCd = tagEmpCd;
//    }
//
//    public Double getTemper() {
//        return temper;
//    }
//
//    public void setTemper(Double temper) {
//        this.temper = temper;
//    }
//
//    public Float getMaskConfidence() {
//        return maskConfidence;
//    }
//
//    public void setMaskConfidence(Float maskConfidence) {
//        this.maskConfidence = maskConfidence;
//    }
//
//    public String getTerminalTyp() {
//        return terminalTyp;
//    }
//
//    public void setTerminalTyp(String terminalTyp) {
//        this.terminalTyp = terminalTyp;
//    }
//
//    public String getDoorCd() {
//        return doorCd;
//    }
//
//    public void setDoorCd(String doorCd) {
//        this.doorCd = doorCd;
//    }
//
//    public String getDoorNm() {
//        return doorNm;
//    }
//
//    public void setDoorNm(String doorNm) {
//        this.doorNm = doorNm;
//    }
//
//    public String getBuildingCd() {
//        return buildingCd;
//    }
//
//    public void setBuildingCd(String buildingCd) {
//        this.buildingCd = buildingCd;
//    }
//
//    public String getBuildingNm() {
//        return buildingNm;
//    }
//
//    public void setBuildingNm(String buildingNm) {
//        this.buildingNm = buildingNm;
//    }
//
//    public String getDeptCd() {
//        return deptCd;
//    }
//
//    public void setDeptCd(String deptCd) {
//        this.deptCd = deptCd;
//    }
//
//    public String getDeptNm() {
//        return deptNm;
//    }
//
//    public void setDeptNm(String deptNm) {
//        this.deptNm = deptNm;
//    }
//
//    public String getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(String createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public String getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(String updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//
//    public String getEntFaceImg() {
//        return entFaceImg;
//    }
//
//    public void setEntFaceImg(String entFaceImg) {
//        this.entFaceImg = entFaceImg;
//    }
}
