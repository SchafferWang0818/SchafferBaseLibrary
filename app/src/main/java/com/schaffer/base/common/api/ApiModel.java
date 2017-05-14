package com.schaffer.base.common.api;

import com.schaffer.base.common.base.BaseModel;

/**
 * Created by a7352 on 2017/5/13.
 */

public class ApiModel extends BaseModel<ApiService> {


	volatile private static ApiModel apiModel;

	@Override
	protected Class<ApiService> getServiceClass() {
		return ApiService.class;
	}

	private ApiModel() {
	}

	public static ApiModel getInstance() {
		if (apiModel == null) {
			synchronized (ApiModel.class) {
				if (apiModel == null) {
					apiModel = new ApiModel();
				}
			}
		}
		return apiModel;
	}
}
