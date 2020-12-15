package com.metaShare.common.param;

public class RegionQueryParam {

		private String name;
		private String regionLevel;
	    
	    private String regionAmt;

		private String securityLevel;
	    
	    private String securityAmt;
	    
	    public String getRegionLevel() {
			return regionLevel;
		}

		public void setRegionLevel(String regionLevel) {
			this.regionLevel = regionLevel;
		}

		public String getRegionAmt() {
			return regionAmt;
		}

		public void setRegionAmt(String regionAmt) {
			this.regionAmt = regionAmt;
		}

		public String getSecurityLevel() {
			return securityLevel;
		}

		public void setSecurityLevel(String securityLevel) {
			this.securityLevel = securityLevel;
		}

		public String getSecurityAmt() {
			return securityAmt;
		}

		public void setSecurityAmt(String securityAmt) {
			this.securityAmt = securityAmt;
		}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
