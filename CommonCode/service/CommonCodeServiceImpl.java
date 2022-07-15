package com.aaihc.crm.biz.groundwork.service;

import com.aaihc.crm.biz.groundwork.domain.CommonCode;
import com.aaihc.crm.biz.groundwork.repository.CommonCodeRepository;
import com.aaihc.crm.biz.groundwork.validation.CommonCodeValidator;
import com.aaihc.crm.core.domain.BaseDomain;
import com.aaihc.crm.core.domain.Search;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * 프로젝트명	: 
 * 개발사		: 
 *
 * <p>CommonCodeServiceImpl (공통 코드 Service Implement)</p>
 *
 * @author 	    : 박지윤
 * date 		: 2021. 03. 04.
 *
 * modifier 	:
 * modify-date  :
 * description  :
 */
@Service
@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
@RequiredArgsConstructor
public class CommonCodeServiceImpl implements CommonCodeService {

    private final CommonCodeRepository commonCodeRepository;
    private CommonCodeValidator commonCodeValidator;

    @Autowired
    public void setCommonCodeValidator(CommonCodeValidator commonCodeValidator) {
        this.commonCodeValidator = commonCodeValidator;
    }

    /**
     * <p>공통 코드을 등록합니다</p>
     *
     * @see CommonCodeService#add(CommonCode)
     * @param commonCode 공통 코드
     * @return 수
     */
    public long add(CommonCode commonCode) {
        long result = 0;
        int depth = commonCode.getDepth(commonCode.getCat());

        commonCode.setDepth(depth);
        commonCodeValidator.add(commonCode);

        commonCodeRepository.save(commonCode);
        result = commonCode.getSeq();

        return result;
    }

    /**
     * <p>지정된 공통 코드을 수정합니다</p>
     *
     * @see CommonCodeService#modify(CommonCode commonCode)
     * @param commonCode 공통 코드
     * @return 수
     */
    public long modify(CommonCode commonCode) {
        long result = 0;

        commonCodeValidator.modify(commonCode);

        CommonCode orgCommonCode = this.findOne(commonCode.getSeq());

        if (orgCommonCode != null) {
            orgCommonCode.setCat(commonCode.getCat());
            orgCommonCode.setCd(commonCode.getCd());
            orgCommonCode.setNm(commonCode.getNm());
            orgCommonCode.setDesc(commonCode.getDesc());
            orgCommonCode.setDepth(commonCode.getDepth());
            orgCommonCode.setUseYn(commonCode.getUseYn());
            orgCommonCode.setStrtYmd(commonCode.getStrtYmd());
            orgCommonCode.setEndYmd(commonCode.getEndYmd());
            orgCommonCode.setModr(commonCode.getModr());
            orgCommonCode.setModYmd(BaseDomain.getCurrYmd());
            orgCommonCode.setModHis(BaseDomain.getCurrHis());

            result = 1;
        }

        return result;
    }

    /**
     * <p>지정된 공통 코드의 sms 내용을 수정합니다</p>
     *
     * @see CommonCodeService#modifyForSms(CommonCode commonCode)
     * @param commonCode 공통 코드
     * @return 수
     */
    public long modifyForSms(CommonCode commonCode) {
        long result = 0;

        CommonCode orgCommonCode = this.findOne(commonCode.getSeq());

        if (orgCommonCode != null) {
            orgCommonCode.setSmsCont(commonCode.getSmsCont());

            result = 1;
        }

        return  result;
    }

    /**
     * <p>지정된 공통 코드을 삭제합니다</p>
     *
     * @see CommonCodeService#remove(long)
     * @param seq 일련번호
     * @return 수
     */
    public long remove(long seq) {
        long result = 0;
        if (seq > 0) {
            commonCodeRepository.deleteById(seq);
            result = 1;
        }

        return result;
    }

    /**
     * <p>지정된 공통 코드을 가져옵니다</p>
     *
     * @see CommonCodeService#findOne(long)
     * @param seq 일련번호
     * @return 공통 코드
     */
    @Transactional(readOnly = true)
    public CommonCode findOne(long seq) {
        return commonCodeRepository.findById(seq);
    }

    /**
     * <p>지정된 공통 코드의 수를 가져옵니다</p>
     *
     * @see CommonCodeService#findTotalCnt(Search)
     * @param search 검색
     * @return 수
     */
    @Transactional(readOnly = true)
    public long findTotalCnt(Search search) {
        return commonCodeRepository.count(search);
    }

    /**
     * <p>공통 코드의 목록을 가져옵니다</p>
     *
     * @see CommonCodeService#findList(Search)
     * @param search 검색
     * @return 목록
     */
    @Transactional(readOnly = true)
    public List<CommonCode> findList(Search search) {
        return commonCodeRepository.findAll(search);
    }

    /**
     * <p>공통 코드의 목록을 가져옵니다</p>
     *
     * @see CommonCodeService#findPage(Search)
     * @param search 검색
     * @return 페이징 목록
     */
    @Transactional(readOnly = true)
    public Page<CommonCode> findPage(Search search) {
        return commonCodeRepository.findPage(search);
    }

}