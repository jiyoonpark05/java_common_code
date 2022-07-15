

package com.aaihc.crm.biz.groundwork.repository;

import asn.util.date.DateUtil;
import asn.util.lang.StringUtil;
import com.aaihc.crm.biz.groundwork.domain.CommonCode;
import com.aaihc.crm.core.domain.Search;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static com.aaihc.crm.biz.groundwork.domain.QCommonCode.commonCode;

/**
 * 프로젝트명	: 
 * 개발사		: 
 *
 * <p>CommonCodeRepository (공통 코드 Repository)</p>
 *
 * @author 	    : 박지윤
 * date 		: 2021. 03. 04.
 *
 * modifier 	:
 * modify-date  :
 * description  :
 */
@Repository
public class CommonCodeRepository {

    private EntityManager em;
    private JPAQueryFactory queryFactory;

    @Autowired
    public void setEntityManager(JpaContext jpaContext) {
        this.em = jpaContext.getEntityManagerByManagedType(CommonCode.class);
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * <p>공통 코드을 등록합니다</p>
     *
     * @param commonCode 공통 코드
     */
    public void save(CommonCode commonCode) {
        em.persist(commonCode);
    }

    /**
     * <p>지정된 공통 코드을 조회합니다.</p>
     *
     * @param seq 일련번호
     * @return Optional CommonCode
     */
    public CommonCode findById(long seq) {
        return em.find(CommonCode.class, seq);
    }

    /**
     * <p>지정된 공통 코드을 삭제합니다.</p>
     *
     * @param seq 일련번호
     * @return 수
     */
    public int deleteById(long seq) {
        int result = 0;

        if (seq > 0) {
            CommonCode commonCode = this.findById(seq);
            if (commonCode != null) {
                em.remove(commonCode);
                result = 1;
            }
        }

        return result;
    }

    /**
     * <p>지정된 공통 코드의 개수를 조회합니다.</p>
     *
     * @param search 검색
     * @return 수
     */
    public long count(Search search) {
        return this.getQuery(search).fetchCount();
    }

    /**
     * <p>지정된 공통 코드의 목록을 조회합니다.</p>
     *
     * @param search 검색
     * @return 목록
     */
    public List<CommonCode> findAll(Search search) {
        return this.getQuery(search).fetch();
    }

    /**
     * <p>지정된 공통 코드의 목록을 조회합니다.</p>
     *
     * @param search 검색
     * @return 목록
     */
    public Page<CommonCode> findPage(Search search) {
        Pageable pageable = search.getPageable();
        QueryResults<CommonCode> result = this.getQuery(search).fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    /**
     * <p>쿼리를 구합니다.</p>
     *
     * @param search 검색
     * @return 값
     */
    private JPQLQuery<CommonCode> getQuery(Search search) {
        JPQLQuery<CommonCode> query = queryFactory.selectFrom(commonCode);

        // 검색조건 추가
        List<Predicate> schConds = this.getSchConds(search);
        if (schConds.size() > 0) {
            query.where(schConds.stream().toArray(Predicate[]::new));
        }

        // 정렬조건 추가
        List<OrderSpecifier> sortConds = this.getSortConds(search);
        if (sortConds.size() > 0) {
            query.orderBy(sortConds.stream().toArray(OrderSpecifier[]::new));
        }

        // 페이징 추가
        Pageable pageable = search.getPageable();
        if (pageable != null) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        return query;
    }

    /**
     * <p>검색조건을 생성합니다.</p>
     *
     * @param search 검색
     * @return 목록
     */
    private List<Predicate> getSchConds(Search search) {
        List<Predicate> schConds = new ArrayList<>();

        String fullCd = search.getString("fullCd");
        if (StringUtil.isNotBlank(fullCd)) {
            String[] cds = StringUtil.split(fullCd, "-");
            if (cds.length == 1) {
                search.add("catEq", "0000");
                search.add("cdEq", cds[0]);
            } else if (cds.length == 2) {
                search.add("catEq", cds[0]);
                search.add("cdEq", cds[1]);
            } else if (cds.length == 3) {
                search.add("catEq", cds[0] + "-" + cds[1]);
                search.add("cdEq", cds[2]);
            } else if (cds.length == 4) {
                search.add("catEq", cds[0] + "-" + cds[1] + "-" + cds[2]);
                search.add("cdEq", cds[3]);
            } else if (cds.length == 5) {
                search.add("catEq", cds[0] + "-" + cds[1] + "-" + cds[2] + "-" + cds[3]);
                search.add("cdEq", cds[4]);
            }
        }

        Object notSeq = search.get("notSeq");
        if (notSeq != null) {
            schConds.add(commonCode.seq.ne((Long) notSeq));
        }

        String cat = search.getString("cat");
        if (StringUtil.isNotBlank(cat)) {
            schConds.add(commonCode.cat.startsWith(cat));
        }

        String catEq = search.getString("catEq");
        if (StringUtil.isNotBlank(catEq)) {
            schConds.add(commonCode.cat.eq(catEq));
        }

        String catLlk = search.getString("catLlk");
        if (StringUtil.isNotBlank(catLlk)) {
            schConds.add(commonCode.cat.startsWith(catLlk));
        }

        String cd = search.getString("cd");
        if (StringUtil.isNotBlank(cd)) {
            schConds.add(commonCode.cd.containsIgnoreCase(cd));
        }

        String cdEq = search.getString("cdEq");
        if (StringUtil.isNotBlank(cdEq)) {
            schConds.add(commonCode.cd.eq(cdEq));
        }

        String nm = search.getString("nm");
        if (StringUtil.isNotBlank(nm)) {
            schConds.add(commonCode.nm.contains(nm));
        }
        String schNm = search.getString("schNm");
        if (StringUtil.isNotBlank(schNm)) {
            schConds.add(commonCode.nm.contains(schNm));
        }

        String nmEq = search.getString("nmEq");
        if (StringUtil.isNotBlank(nmEq)) {
            schConds.add(commonCode.nm.eq(nmEq));
        }

        String useYn = search.getString("useYn");
        if (StringUtil.isNotBlank(useYn)) {
            schConds.add(commonCode.useYn.eq(useYn));
        }
        String schUseYn = search.getString("schUseYn");
        if (StringUtil.isNotBlank(schUseYn)) {
            schConds.add(commonCode.useYn.eq(schUseYn));
        }

        String depth = search.getString("depth");
        if (StringUtil.isNotBlank(depth)) {
            schConds.add(commonCode.depth.eq(Integer.valueOf(depth)));
        }

        String notDepth = search.getString("notDepth");
        if (StringUtil.isNotBlank(notDepth)) {
            schConds.add(commonCode.depth.ne(Integer.valueOf(notDepth)));
        }

        String fullCdLk = search.getString("fullCdLk");
        if (StringUtil.isNotBlank(fullCdLk)) {
            schConds.add(commonCode.cat.concat("-").concat(commonCode.cd).startsWith(fullCdLk));
        }

        String periodYn = search.getString("periodYn");
        if (StringUtil.isNotBlank(periodYn) && StringUtil.equals("Y", periodYn)) {
            String currYmd = DateUtil.getCurrentDate();
            schConds.add(commonCode.strtYmd.loe(currYmd).and(commonCode.endYmd.goe(currYmd)));
        }

        return schConds;
    }

    /**
     * <p>정렬조건을 생성합니다.</p>
     *
     * @param search 검색
     * @return 목록
     */
    private List<OrderSpecifier> getSortConds(Search search) {
        List<OrderSpecifier> orderSpecifiers = search.getQdslSortConds(commonCode);

        if (orderSpecifiers.size() == 0) {
            // 기존정렬을 코드 순으로 함.
            StringExpression currency = new CaseBuilder().when(commonCode.cat.eq("0000")).then(commonCode.cd + "-" + commonCode.cd).otherwise(commonCode.cat+ "-" + commonCode.cd);
            orderSpecifiers.add(currency.asc());
        }

        return orderSpecifiers;
    }

}
