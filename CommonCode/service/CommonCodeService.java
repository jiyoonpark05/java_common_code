package com.aaihc.crm.biz.groundwork.service;

import com.aaihc.crm.biz.groundwork.domain.CommonCode;
import com.aaihc.crm.core.domain.Search;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 프로젝트명	: 
 * 개발사		: 
 *
 * <p>CommonCode Service (공통 코드 Service)</p>
 *
 * @author 	    : 박지윤
 * date 		: 2021. 03. 04.
 *
 * modifier 	:
 * modify-date  :
 * description  :
 */
public interface CommonCodeService {

    /**
     * <p>공통 코드을 등록합니다</p>
     *
     * @param commonCode 공통 코드
     * @return 수
     */
    public long add(CommonCode commonCode);

    /**
     * <p>지정된 공통 코드을 수정합니다</p>
     *
     * @param commonCode 공통 코드
     * @return 수
     */
    public long modify(CommonCode commonCode);

    /**
     * <p>지정된 공통 코드의 sms 내용을 수정합니다</p>
     *
     * @param commonCode 공통 코드
     * @return 수
     */
    public long modifyForSms(CommonCode commonCode);

    /**
     * <p>지정된 공통 코드을 삭제합니다</p>
     *
     * @param seq 일련번호
     * @return 수
     */
    public long remove(long seq);

    /**
     * <p>지정된 공통 코드을 가져옵니다</p>
     *
     * @param seq 일련번호
     * @return 공통 코드
     */
    public CommonCode findOne(long seq);

    /**
     * <p>지정된 공통 코드의 수를 가져옵니다</p>
     *
     * @param search 검색
     * @return 수
     */
    public long findTotalCnt(Search search);

    /**
     * <p>지정된 공통 코드의 목록을 가져옵니다</p>
     *
     * @param search 검색
     * @return 목록
     */
    public List<CommonCode> findList(Search search);

    /**
     * <p>공통 코드의 목록을 가져옵니다</p>
     *
     * @param search 검색
     * @return 페이징 목록
     */
    public Page<CommonCode> findPage(Search search);

}