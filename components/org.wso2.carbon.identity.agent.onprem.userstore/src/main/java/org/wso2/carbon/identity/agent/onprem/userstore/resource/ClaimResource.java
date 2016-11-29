/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.identity.agent.onprem.userstore.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.identity.agent.onprem.userstore.config.ClaimConfiguration;
import org.wso2.carbon.identity.agent.onprem.userstore.constant.CommonConstants;
import org.wso2.carbon.identity.agent.onprem.userstore.manager.claim.ClaimManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *  Claim REST endpoint.
 *  This will be available at https://localhost:8888/wso2agent/claims
 */
@Api(value = CommonConstants.APPLICATION_CONTEXT_PATH + "claims")
@SwaggerDefinition(
        info = @Info(
                title = "Claims Endpoint Swagger Definition", version = "1.0",
                description = "The endpoint which is used to manage claims.",
                license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0")
        )
)
@Path(CommonConstants.APPLICATION_CONTEXT_PATH + "/claims")
public class ClaimResource {
    private static Logger log = LoggerFactory.getLogger(UserResource.class);
    /**
     * @param uri URI of the Claim whose mapped attributes are required.     *
     * @return Attribute mapped to the given claimURI.
     */
    @GET
    @Path("attribute/")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Return the attribute name mapped to the claimURI. ",
            notes = "Returns HTTP 404 if particular claim mapping not found")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "{ uri_value : attribute_name }"),
            @ApiResponse(code = 404, message = "None")})
    public Response getClaimAttribute(@ApiParam(value = "uri", required = true)
                                      @QueryParam("uri") String uri) {

        String attributeID = null;
        ClaimManager claimManager = new ClaimManager(ClaimConfiguration.getConfiguration().getClaimMap());
        attributeID = claimManager.getClaimAttribute(uri);
        if (attributeID != null) {
            JSONObject returnObject = new JSONObject();
            returnObject.put(uri, attributeID);
            return Response.status(Response.Status.OK).entity(returnObject.toString()).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * @return The list of enabled claim URIs.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Return the list of enabled claim URIs. ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "{urilist:[uri1, uri2, ...]}")
    })
    public Response getAllUserNames() {
        ClaimManager claimManager = new ClaimManager(ClaimConfiguration.getConfiguration().getClaimMap());
        String[] uris = claimManager.
                doListClaims();
        JSONObject jsonObject = new JSONObject();
        JSONArray uriArray = new JSONArray(uris);
        jsonObject.put("urilist", uriArray);
        return Response.status(Response.Status.OK).entity(jsonObject.toString()).build();
    }

}
