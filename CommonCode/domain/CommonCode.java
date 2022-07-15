HHpackage com.aaihc.crm.biz.groundwork.domain;

import asn.util.date.DateFormatUtil;
import asn.util.lang.StringUtil;
import asn.util.num.NumberUtil;
import com.aaihc.crm.core.config.ConfigProperty;
import com.aaihc.crm.core.domain.BaseEntityTime;
import com.aaihc.crm.core.domain.BaseRgstrNm;
import lombok.*;
import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 프로젝트명	: 
 * 개발사		: 
 *
 * <p>CommonCode (공통 코드 Domain)</p>
 *
 * @author      : 박지윤
 * date 		: 2021. 03. 04.
 * <p>
 * modifier 	:
 * modify-date  :
 * description  :
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Entity(name = "t_gw_comm_cd")
@SequenceGenerator(name = "t_seq_gw_comm_cd_gen", sequenceName = "t_seq_gw_comm_cd", allocationSize = 1)
@EntityListeners(AuditingEntityListener.class)R
@AttributeOverrides({
        @AttributeOverride(name = "rgstYmd", column = @Column(name = "cc_rgst_ymd"))
        , @AttributeOverride(name = "rgstHis", column = @Column(name = "cc_rgst_his"))
        , @AttributeOverride(name = "modYmd", column = @Column(name = "cc_mod_ymd"))
        , @AttributeOverride(name = "modHis", column = @Column(name = "cc_mod_his"))
})
public class CommonCode extends BaseEntityTime {
    private static final long serialVersionUID = 1L;

    public static String CATE_CNSL_CUST_TP = ConfigProperty.getString("groupwork.commonCodeCate.counsel.custTp");
    public static String CATE_CNSL_CNSL_SVC_TP = ConfigProperty.getString("groupwork.commonCodeCate.counsel.cnslSvcTp");
    public static String CATE_CNSL_SKILL_GROUP = ConfigProperty.getString("groupwork.commonCodeCate.counsel.skillGroup");
    public static String CATE_CNSL_CNSL_EXTNL_TP = ConfigProperty.getString("groupwork.commonCodeCate.counsel.cnslExtnlTp");
    public static String CATE_CNSL_CNSL_CAT = ConfigProperty.getString("groupwork.commonCodeCate.counsel.cnslCat");
    public static String CATE_CNSL_CNSL_TRET_RSLT = ConfigProperty.getString("groupwork.commonCodeCate.counsel.cnslTretRslt");
    public static String CATE_CNSL_CNSL_STFDG = ConfigProperty.getString("groupwork.commonCodeCate.counsel.cnslStfdg");
    public static String CATE_CNSL_CNSL_AFFT_MDCO = ConfigProperty.getString("groupwork.commonCode.counsel.medicalOffice");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "t_seq_gw_comm_cd_gen")
    @Column(name = "cc_seq")
    private long seq; // 공통 코드 일련번호
    @Column(name = "cc_cat", length = 50)
    private String cat; // 분류
    @Column(name = "cc_cd", length = 50)
    private String cd; // 코드
    @Column(name = "cc_nm", length = 50)
    private String nm; // 명
    @Column(name = "cc_desc", length = 500)
    private String desc; // 설명
    @Column(name = "cc_depth")
    private int depth; // 깊이
    @Column(name = "cc_sms_cont", length = 2000)
    private String smsCont; // 문자내용
    @Column(name = "cc_use_yn", length = 1)
    private String useYn; // 사용여부
    @Column(name = "cc_strt_ymd", length = 8)
    private String strtYmd; // 시작일
    @Column(name = "cc_end_ymd", length = 8)
    private String endYmd; // 종료일

    @CreatedBy
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "cc_rgstr_id"))
            , @AttributeOverride(name = "nm", column = @Column(name = "cc_rgstr_nm"))
    })
    private BaseRgstrNm rgstr;

    @CreatedBy
    // @LastModifiedBy
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "cc_modr_id"))
            , @AttributeOverride(name = "nm", column = @Column(name = "cc_modr_nm"))
    })
    private BaseRgstrNm modr;

    @Transient
    private List<CommonCode> commonCodes = new ArrayList<>();

    public CommonCode(List<CommonCode> commonCodes) {
        this.commonCodes = commonCodes;
    }

    /**
     * <p>공통코드 구분 맵</p>
     *
     * @param commonCodes 공통코드 목록
     * @return 맵
     */
    public static ListOrderedMap getCatMap(List<CommonCode> commonCodes) {
        ListOrderedMap result = new ListOrderedMap();

        if (commonCodes != null) {
            for (CommonCode commonCode : commonCodes) {
                result.put(commonCode.getCd(), "[" + commonCode.getCd() + "]" + commonCode.getNm());
            }
        }

        return result;
    }

    /**
     * <p>공통코드 구분 맵 (카테고리-코드)</p>
     *
     * @param cat 카테고리
     * @param useYn 사용여부
     * @return 맵
     */
    public TreeMap getCatCdMap(String cat, String useYn) {
        TreeMap<String, Object> result = new TreeMap<>();

        if (commonCodes != null && commonCodes.size() > 0) {
            result = commonCodes.stream().filter(p -> StringUtil.equals(p.getCat(), cat) && p.isUseYnWithDt(useYn))
                    .collect(Collectors.toMap(CommonCode::getCatCd, CommonCode::getNm, (p1, p2) -> p1, TreeMap::new));
        }

        return result;
    }

    /**
     * <p>코드 맵을 구합니다.</p>
     *
     * @param depth depth
     * @param cat 카테고리
     * @param useYn 사용여부
     * @return 목록
     */
    public Map getMap(int depth, String cat, String useYn) {
        Map<String, Object> result = new HashMap<>();

        if (StringUtil.isNotBlank(cat) && commonCodes.size() > 0) {
            if (depth == 1) {
                result = commonCodes.stream().filter(p -> StringUtil.equals(p.getCat(), cat) && p.isUseYnWithDt(useYn)).collect(Collectors.toMap(CommonCode::getCd, CommonCode::getNm, (p1, p2) -> p1));
            } else {
                result = commonCodes.stream().filter(p -> StringUtil.equals(p.getCat(), cat) && p.isUseYnWithDt(useYn)).collect(Collectors.toMap(CommonCode::getCd, CommonCode::getNm));
            }
        }

        return result;
    }

    /**
     * <p>사용여부 및 기간 포함여부를 구합니다.</p>Ó
     * @param useYn 사용여부
     * @return 값
     */
    private boolean isUseYnWithDt(String useYn) {
        if (StringUtil.equals("N", useYn)) {
            return StringUtil.equals("N", this.getUseYn());
        }

        if (StringUtil.equals("Y", useYn)) {
            int currYmd = NumberUtil.toInt(DateFormatUtil.format(new Date(), "yyyyMMdd"));
            int strtYmd = NumberUtil.toInt(this.getStrtYmd());
            int endYmd = NumberUtil.toInt(this.getEndYmd());
            return StringUtil.equals("Y", this.getUseYn()) && (strtYmd <= currYmd && currYmd <= endYmd);
        }

        return false;
    }

    /**
     * <p>코드 맵을 구합니다.</p>
     *
     * @param cat 카테고리
     * @param  useYn 사용여부
     * @return 목록
     */
    public List<CommonCode> gets(String cat, String useYn) {
        List<CommonCode> results = new ArrayList<>();

        if (StringUtil.isNotBlank(cat) && commonCodes.size() > 0) {
            results = commonCodes.stream().filter(p -> StringUtil.equals(p.getCat(), cat) && p.isUseYnWithDt(useYn)).collect(Collectors.toList());
        }

        return results;
    }

    /**
     * <p>전체 코드를 구합니다.</p>
     *
     * @param dlmt 구분자
     * @return 값
     */
    public String getFullCd(String dlmt)  {
        return StringUtil.toString(this.getCat()) + dlmt + StringUtil.toString(this.getCd());
    }

    /**
     * <p>코드명을 구합니다.</p>
     *
     * @param isWith1Depth isWith1Depth
     * @param cd 코드
     * @param dlmt 구분자
     * @return 값
     */
    public String getFullNm(boolean isWith1Depth, String cd, String dlmt) {
        String val = "";

        if (StringUtil.isNotBlank(cd)) {
            String[] cds = StringUtil.split(cd, "-");

            switch (cds.length) {
                case 1:
                    val = this.getNm("0000", cds[0]);
                    break;
                case 2:
                    val = (isWith1Depth ? this.getNm("0000", cds[0]) + dlmt : "") + this.getNm(cds[0], cds[1]);
                    break;
                case 3:
                    val = (isWith1Depth ? this.getNm("0000", cds[0]) + dlmt : "") + this.getNm(cds[0], cds[1]) + dlmt + this.getNm(cds[0] + "-" + cds[1], cds[2]);
                    break;
                case 4:
                    val = (isWith1Depth ? this.getNm("0000", cds[0]) + dlmt : "") + this.getNm(cds[0], cds[1]) + dlmt + this.getNm(cds[0] + "-" + cds[1], cds[2]) + dlmt + this.getNm(cds[0] + "-" + cds[1] + "-" + cds[2], cds[3]);
                    break;
                case 5:
                    val = (isWith1Depth ? this.getNm("0000", cds[0]) + dlmt : "") + this.getNm(cds[0], cds[1]) + dlmt + this.getNm(cds[0] + "-" + cds[1], cds[2]) + dlmt + this.getNm(cds[0] + "-" + cds[1] + "-" + cds[2], cds[3]) + dlmt + this.getNm(cds[0] + "-" + cds[1] + "-" + cds[2] + "-" + cds[3], cds[4]);
                    break;
                default: break;
            }
        }

        return  val;
    }

    /**
     * <p>카테고리-코드명을 구합니다.</p>
     *
     * @return 값
     */
    public String getCatCd() {
        return this.getCat() + "-" + this.getCd();
    }

    /**
     * <p>코드명을 구합니다.</p>
     *
     * @param cd 코드
     * @return 값
     */
    public String getNm(String cd) {
        String val = "";

        if (StringUtil.isNotBlank(cd)) {
            String[] cds = StringUtil.split(cd, "-");

            switch (cds.length) {
                case 1:
                    val = this.getNm("0000", cds[0]);
                    break;
                case 2:
                    val = this.getNm(cds[0], cds[1]);
                    break;
                case 3:
                    val = this.getNm(cds[1], cds[2]);
                    break;
                case 4:
                    val = this.getNm(cds[2], cds[3]);
                    break;
                case 5:
                    val = this.getNm(cds[3], cds[4]);
                    break;
                default:
                    break;
            }
        }

        return  val;
    }

    /**
     * <p>코드명을 구합니다.</p>
     *
     * @param dept 깊이
     * @param cd 코드
     * @return 값
     */
    public String getNmDepth(int dept, String cd) {
        String val = "";

        if (StringUtil.isNotBlank(cd)) {
            String[] cds = StringUtil.split(cd, "-");

            if (dept > cds.length) {
                return val;
            }

            switch (dept) {
                case 1:
                    val = this.getNm1(cds[0]);
                    break;
                case 2:
                    val = this.getNm2(cds[0], cds[1]);
                    break;
                case 3:
                    val = this.getNm3(cds[0], cds[1], cds[2]);
                    break;
                case 4:
                    val = this.getNm4(cds[0], cds[1], cds[2], cds[3]);
                    break;
                default:
                    break;
            }
        }

        return  val;
    }

    /**
     * <p>코드명을 구합니다.</p>
     *
     * @param cat 코드1
     * @param cd 코드2
     * @return 값
     */
    public String getNm(String cat, String cd) {
        String val = "";

        if (StringUtil.isNotBlank(cat, cd)) {
            Optional<CommonCode> commonCodeOptional = commonCodes.stream().filter(p -> p.equalsCd(cat, cd)).findFirst();
            if (commonCodeOptional.isPresent()) {
                val = commonCodeOptional.get().getNm();
            }
        }

        return  val;
    }

    /**
     * <p>1depth 코드명을 구합니다.</p>
     *
     * @param cd1 코드1
     * @return 값
     */
    public String getNm1(String cd1) {
        String val = "";

        if (StringUtil.isNotBlank(cd1) && commonCodes.size() > 0) {
            String cat = StringUtil.left(cd1, 1) + "00";

            Optional<CommonCode> commonCodeOptional = commonCodes.stream().filter(p -> p.equalsCd(cat, cd1)).findFirst();
            if (commonCodeOptional.isPresent()) {
                val = commonCodeOptional.get().getNm();
            }
        }

        return val;
    }

    /**
     * <p>2depth 코드명을 구합니다.</p>
     *
     * @param cd1 코드1
     * @param cd2 코드2
     * @return 값
     */
    public String getNm2(String cd1, String cd2) {
        String val = "";

        if (StringUtil.isNotBlank(cd1, cd2) && commonCodes.size() > 0) {
            Optional<CommonCode> commonCodeOptional = commonCodes.stream().filter(p -> p.equalsCd(cd1, cd2)).findFirst();
            if (commonCodeOptional.isPresent()) {
                val = commonCodeOptional.get().getNm();
            }
        }

        return val;
    }

    /**
     * <p>3depth 코드명을 구합니다.</p>
     *
     * @param cd1 코드1
     * @param cd2 코드2
     * @param cd3 코드3
     * @return 값
     */
    public String getNm3(String cd1, String cd2, String cd3) {
        String val = "";

        if (StringUtil.isNotBlank(cd1, cd2, cd3) && commonCodes.size() > 0) {
            Optional<CommonCode> commonCodeOptional = commonCodes.stream().filter(p -> p.equalsCd(cd1 + "-" + cd2, cd3)).findFirst();
            if (commonCodeOptional.isPresent()) {
                val = commonCodeOptional.get().getNm();
            }
        }

        return val;
    }

    /**
     * <p>4depth 코드명을 구합니다.</p>
     *
     * @param cd1 코드1
     * @param cd2 코드2
     * @param cd3 코드3
     * @param cd4 코드4
     * @return 값
     */
    public String getNm4(String cd1, String cd2, String cd3, String cd4) {
        String val = "";

        if (StringUtil.isNotBlank(cd1, cd2, cd3, cd4) && commonCodes.size() > 0) {
            Optional<CommonCode> commonCodeOptional = commonCodes.stream().filter(p -> p.equalsCd(cd1 + "-" + cd2 + "-" + cd3, cd4)).findFirst();
            if (commonCodeOptional.isPresent()) {
                val = commonCodeOptional.get().getNm();
            }
        }


        return val;
    }

    /**
     * <p> depth를 구합니다.</p>
     *
     * @param cat 카테고리
     * @return depth
     * */
    public int getDepth(String cat){
        int depth = 0;

        if (StringUtil.isNotBlank(cat)) {
            String[] cats = StringUtil.split(cat, "-");

            depth = cats.length + 1;
        }

        return depth;
    }

    /**
     * <p>1depth 일치여부를 구합니다.</p>
     *
     * @param cat 카테고리
     * @param cd  코드
     * @return 값
     */
    private boolean equalsCd(String cat, String cd) {
        if (StringUtil.isNotBlank(cat, cd)) {
            return StringUtil.equals(this.getCat(), cat) && StringUtil.equals(this.getCd(), cd);
        }

        return false;
    }

    public boolean isVal(String catCd, String cd, String useYn) {
        if (StringUtil.isNotBlank(catCd, cd, useYn)) {
            Optional<CommonCode> commonCodeOptional = this.getCommonCodes().stream().filter(p -> StringUtil.startsWith(p.getCat(), catCd)
                    && StringUtil.equals(cd, p.getCat() + "-" + p.getCd()) && StringUtil.equals(useYn, p.getUseYn())).findFirst();

            if (commonCodeOptional.isPresent()) {
                return true;
            }
        } else if (StringUtil.isNotBlank(catCd, cd)) {
            Optional<CommonCode> commonCodeOptional = this.getCommonCodes().stream().filter(p -> StringUtil.startsWith(p.getCat(), catCd)
                    && StringUtil.equals(cd, getCat() + "-" + p.getCd())).findFirst();

            if (commonCodeOptional.isPresent()) {
                return true;
            }
        }

        return false;
    }

    public int getChildCnt(String fullCd, int depth) {
        return (int) this.getCommonCodes().stream().filter(p -> StringUtil.equals(fullCd, p.getCat()) && p.getDepth() == depth).count();
    }
}

