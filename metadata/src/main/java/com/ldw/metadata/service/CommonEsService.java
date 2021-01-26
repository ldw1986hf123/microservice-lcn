package com.ldw.metadata.service;

import com.alibaba.fastjson.JSONObject;
import com.ldw.metadata.dto.IdDTO;
import com.ldw.metadata.pageHelper.PageBean;
import com.ldw.metadata.vo.CommonSearchVO;
import org.elasticsearch.action.update.UpdateResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * ClassName:CommonEsService <br/>
 * Function: 通用的elasticsearch服务接口类. <br/>
 * Reason: . <br/>
 * Date: 2020年8月31日 下午2:50:18 <br/>
 *
 * @author WangXf
 * @see
 * @since JDK 1.8
 */
public interface CommonEsService {

    /**
     * @param index
     * @param id
     * @param jsonEntity
     * @param timeout
     * @return 添加结果状态
     * @desc 添加 es json实体对象
     */
    public Boolean add(String index, String id, String jsonEntity, long timeout);

    /**
     * @param index
     * @param id
     * @param jsonEntity
     * @param timeout
     * @return 修改结果状态
     * @desc 修改 es json实体对象
     */
    public Boolean update(String index, String id, String jsonEntity, long timeout);

    /**
     * @param index
     * @param mustWhere
     * @param shouldWhere
     * @param updateScript
     * @param timeout
     * @return
     * @desc 通过查询条件修改 es json实体对象
     */
    public Boolean updateByQuery(String index, Map<String, Object> mustWhere, Map<String, Object> shouldWhere,
                                 String updateScript, long timeout);

    /**
     * @param index
     * @param id
     * @param timeout
     * @return 删除结果状态
     * @desc 通过id删除 es json实体对象
     */
    public Boolean delete(String index, String id, long timeout);

    /**
     * @param index
     * @param mustWhere
     * @param shouldWhere
     * @param timeout
     * @return
     * @desc 通过查询条件删除 es json实体对象
     */
    public Boolean deleteByQuery(String index, Map<String, Object> mustWhere, Map<String, Object> shouldWhere,
                                 long timeout);

    /**
     * @param index           索引
     * @param from            当前页
     * @param size            每页显示条数
     * @param mustWhere       and查询条件
     * @param shouldWhere     or查询条件
     * @param sortFieldsToAsc 排序字段列表
     * @param includeFields   结果返回字段列表
     * @param excludeFields   结果不返回字段列表
     * @param timeOut
     * @return 结果集合列表
     * @desc 分页查询索引
     */
    public Map<String, Object> searchIndex(String index, Integer from, Integer size, Map<String, Object> mustWhere,
                                           Map<String, Object> shouldWhere, Map<String, Boolean> sortFieldsToAsc, String[] includeFields,
                                           String[] excludeFields, Long timeOut, Boolean pagingFlag);

    public <T> T findOne(CommonSearchVO<T> data);

    UpdateResponse updateDoc(String index, String docId, Map map) throws IOException;

    /**
     * 根据索引查找，不分页返回
     *
     * @param index
     * @param filterCondition
     * @return
     */
    List<JSONObject> findByMap(String index, Map<String, String> filterCondition);

    /**
     * 分页查找
     *
     * @param index
     * @param filterCondition
     * @param page
     * @param size
     * @return
     */
    Map<String, Object> findByMap(String index, Map<String, String> filterCondition, Integer page, Integer size);

    /**
     * 查找满足条件的一定范围内的数据
     *
     * @param index
     * @param mustWhere
     * @param rangeField
     * @param max
     * @param min
     * @return
     */
    List<JSONObject> findByMapAndRange(String index, Map<String, String> mustWhere, String rangeField, Object max, Object min);


    Boolean createIndex(String indexName);

    /**
     * 批量保存数据
     *
     * @param index   索引
     * @param sources 数据集合
     * @param timeOut 超时时间
     * @return 是否保存成功
     */
    <T extends IdDTO> Boolean batchAdd(String index, List<T> sources, Integer timeOut);

    /**
     * 分页按条件查询，且可以根据传入字段排序
     *
     * @param index
     * @param mustWhere
     * @param page
     * @param size
     * @param sortedFiled
     * @param sorted      AES 生产  DESC 降序
     * @return
     */
    Map<String, Object> findByMap(String index, Map<String, String> mustWhere, Integer page, Integer size, String sortedFiled, String sorted);

    /**
     * 根据groupField 分组，然后求sumField 的和
     * 然后倒序返回
     *
     * @param index
     * @param groupField
     * @param sumField
     * @return
     */
    List<Map<String, Long>> sumGroupByField(String index, Map<String, String> mustWhere, String groupField, String sumField);

    /**
     * 根据条件查询，返回匹配matchAll  里面条件的记录，可以包含中英文搜索
     *
     * @param index
     * @param mustWhere
     * @param matchAll
     * @param page
     * @param size
     * @param sortedFiled
     * @param sorted
     * @return
     */
    Map<String, Object> findByMap(String index, Map<String, String> mustWhere, Map<String, String> matchAll, Integer page, Integer size, String sortedFiled, String sorted);

    /**
     * 跟ID查找，指挥返回一条记录
     * @param index
     * @param id
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T findById(String index, String id, Class<T> clazz);


    /**
     * 返回返回分页查找
     * @param index
     * @param mustWhere
     * @param page
     * @param size
     * @param clazz
     * @param <T>
     * @return
     */
    <T> PageBean<T> findByMap(String index, Map<String, String> mustWhere, Integer page, Integer size, Class<T> clazz);

}
