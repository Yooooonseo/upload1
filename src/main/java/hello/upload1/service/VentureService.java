package hello.upload1.service;

import hello.upload1.Repository.VentureListInfoRepository;
import hello.upload1.Repository.VentureRepository;
import hello.upload1.domain.UploadFile;
import hello.upload1.domain.Venture;
import hello.upload1.domain.VentureListInfo;
import hello.upload1.dto.VentureForm;
import hello.upload1.dto.VentureListInfoForm;
import hello.upload1.exception.ResourceNotFoundException;
import hello.upload1.file.FileStore;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class VentureService {

    private final VentureRepository ventureRepository;
    public final VentureListInfoRepository ventureListInfoRepository;
    private final FileStore fileStore;
    private final VentureStatusService ventureStatusService;

    public Long saveVenture(VentureListInfoForm form) throws IOException {
        UploadFile attachFile = fileStore.storeFile(form.getAttachFile());

        JSONObject ventureStatus = ventureStatusService.getCompanyNum(form.getVentureNumber());
        if (ventureStatus != null) {
            form.setB_stt((String) ventureStatus.get("b_stt"));
        }

        /*// 1. Venture 테이블에 데이터 저장
        Venture venture = new Venture();
        venture.setVentureName(form.getVentureName());
        venture.setOwnerName(form.getOwnerName());
        venture.setVentureNumber(form.getVentureNumber());
        venture.setAttachFile(attachFile);
        venture.setB_stt(form.getB_stt());

        Venture savedVenture = ventureRepository.save(venture);*/


        // 2. VentureListInfo 테이블에 저장
        VentureListInfo ventureListInfo = new VentureListInfo();

        ventureListInfo.setCode(form.getCode());
        ventureListInfo.setMainProduct(form.getMainProduct());
        ventureListInfo.setArea(form.getArea());
        ventureListInfo.setAddress(form.getAddress());
        ventureListInfo.setRegistInstitution(form.getRegistInstitution());
        ventureListInfo.setEndDate(form.getEndDate());
        ventureListInfo.setRegistType(form.getRegistType());
        ventureListInfo.setTypeName(form.getTypeName());
        ventureListInfo.setTypeName_spc(form.getTypeName_spc());
        ventureListInfo.setName(form.getName());
        ventureListInfo.setOwner(form.getOwner());
        ventureListInfo.setStartDate(form.getStartDate());
        ventureListInfo.setVentureNumber(form.getVentureNumber());
        ventureListInfo.setAttachFile(attachFile);
        ventureListInfo.setB_stt(form.getB_stt());

        VentureListInfo savedVentureListInfo = ventureListInfoRepository.save(ventureListInfo);


        return savedVentureListInfo.getId();
    }

    public VentureListInfo getVentureById(Long id) {
        return ventureListInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venture not found with id " + id));
    }

    public ResponseEntity<Resource> downloadAttach(Long id) throws MalformedURLException {
        //Venture venture = getVentureById(id);
        VentureListInfo ventureListInfo = getVentureById(id);

        String storeFileName = ventureListInfo.getAttachFile().getStoreFileName();
        String uploadFileName = ventureListInfo.getAttachFile().getUploadFileName();

        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));

        log.info("uploadFileName={}", uploadFileName);

        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}
