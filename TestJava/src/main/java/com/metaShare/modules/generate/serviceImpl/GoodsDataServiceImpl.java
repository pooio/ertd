package com.metaShare.modules.generate.serviceImpl;


@Service
public class GoodsDataServiceImpl extends ServiceImpl<com.metaShare.modules.generate.dao.GoodsDataDao,com.metaShare.modules.generate.entity.GoodsData> implements com.metaShare.modules.generate.service.GoodsDataService {
	
	@Autowired
	private SysDictDao sysDictDao;
	
	@Override
	@Transactional
	public boolean insert(com.metaShare.modules.generate.entity.GoodsData entity) {
		try {
		    super.insert(entity);
	        return true;
	
	@Override
	@Transactional
	public boolean updateById(com.metaShare.modules.generate.entity.GoodsData entity) {
		try {
		    super.updateById(entity);
	        return true;
	
	@Override
	@Transactional
	public boolean deleteById(Serializable id) {
		try {
		    super.deleteById(id);
	        return true;
	
	@Override
	public com.metaShare.modules.generate.entity.GoodsData selectById(Serializable id) {
		com.metaShare.modules.generate.entity.GoodsData entity = super.selectById(id);
		try {
	        } catch (Exception e) {
	return entity;
	    }
	@Override
	public Result selectList(int pageNum,int pageSize) {
		try {
		   List<com.metaShare.modules.generate.entity.GoodsData> listInfo = this.selectList(null);
		    int total = listInfo.size();
		    PageDTO pageDTO = new PageTool<com.metaShare.modules.generate.entity.GoodsData>().getPage(listInfo, pageSize, pageNum);
		    return Result.resultInfo(ResultCode.SUCCESS,total,pageDTO.getData());
		} catch (Exception e) {
		    e.printStackTrace();
		    return Result.resultInfo(ResultCode.FAILURE,"获取列表数据失败");
		}
	}
}