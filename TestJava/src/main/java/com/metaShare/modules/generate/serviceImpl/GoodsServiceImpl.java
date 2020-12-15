package com.metaShare.modules.generate.serviceImpl;


@Service
public class GoodsServiceImpl extends ServiceImpl<com.metaShare.modules.generate.dao.GoodsDao,com.metaShare.modules.generate.entity.Goods> implements com.metaShare.modules.generate.service.GoodsService {
	
	@Autowired
	private SysDictDao sysDictDao;
	
	@Autowired
	private com.metaShare.modules.generate.service.GoodsDataService  DatasService;
	
	@Override
	@Transactional
	public boolean insert(com.metaShare.modules.generate.entity.Goods entity) {
		try {
		    super.insert(entity);
		    if(entity.getDatas() != null) {
		        entity.getDatas().setGoodsId(entity.getId()); 
		        }
		         DatasService.insert(entity.getDatas());
		
	        return true;
	
	@Override
	@Transactional
	public boolean updateById(com.metaShare.modules.generate.entity.Goods entity) {
		try {
		    super.updateById(entity);
		    if(entity.getDatas() != null) {
		        if(StringUtils.isEmpty(entity.getDatas().getId())){
		            DatasService.insert(entity.getDatas());
		        } else {
		            DatasService.updateById(entity.getDatas());
		        }
		    }
		
	        return true;
	
	@Override
	@Transactional
	public boolean deleteById(Serializable id) {
		try {
		    super.deleteById(id);
		    Wrapper DatasWrapper = Condition.create().eq("Goods_id", id);
		    DatasService.delete(DatasWrapper);
		
	        return true;
	
	@Override
	public com.metaShare.modules.generate.entity.Goods selectById(Serializable id) {
		com.metaShare.modules.generate.entity.Goods entity = super.selectById(id);
		try {
		    Wrapper DatasWrapper = Condition.create().eq("Goods_id", id).eq("deleted",false);
		    com.metaShare.modules.generate.entity.GoodsData Datas = DatasService.selectOne(DatasWrapper);
		    entity.setDatas(Datas);
		
	        } catch (Exception e) {
	return entity;
	    }
	@Override
	public Result selectList(int pageNum,int pageSize) {
		try {
		   List<com.metaShare.modules.generate.entity.Goods> listInfo = this.selectList(null);
		    int total = listInfo.size();
		    PageDTO pageDTO = new PageTool<com.metaShare.modules.generate.entity.Goods>().getPage(listInfo, pageSize, pageNum);
		    return Result.resultInfo(ResultCode.SUCCESS,total,pageDTO.getData());
		} catch (Exception e) {
		    e.printStackTrace();
		    return Result.resultInfo(ResultCode.FAILURE,"获取列表数据失败");
		}
	}
	@Override
	@Override
}