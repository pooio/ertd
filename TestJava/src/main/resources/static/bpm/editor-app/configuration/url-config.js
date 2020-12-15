/*
 * Activiti Modeler component part of the Activiti project
 * Copyright 2005-2014 Alfresco Software, Ltd. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
var KISBPM = KISBPM || {};

KISBPM.URL = {

    getModel: function(modelId) {
        return ACTIVITI.CONFIG.contextRoot + 'bpm/modeler/model/' + modelId + '/json';
    },

    getStencilSet: function() {
    	var str=ACTIVITI.CONFIG.contextRoot + 'bpm/modeler/editor/stencilset?version=' + Date.now();
        return str;
    },

    putModel: function(modelId) {
        return ACTIVITI.CONFIG.contextRoot + 'bpm/modeler/model/' + modelId + '/save';
    },
    getModelList: function(){
    	//return ACTIVITI.CONFIG.contextRoot + "#/bpm/modeler";
    	//return "http://210.12.194.163:5416/#/bpm/modeler";
    	return "http://10.0.1.23:3001/#/process-modal";
    	//return "http://117.34.118.114:9046/#/bpm/modeler";
    }
};