/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import static org.dspace.app.rest.utils.RegexUtils.REGEX_REQUESTMAPPING_IDENTIFIER_AS_UUID;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.model.hateoas.ItemResource;
import org.dspace.app.rest.repository.ItemRestRepository;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.app.rest.utils.Utils;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ControllerUtils;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * This RestController takes care of the modification and retrieval of Collection's Item templates
 * Contrary to CollectionItemtemplateController, this class will receive the UUID of an Item template
 */
@RestController
@RequestMapping("/api/core/itemtemplates" + REGEX_REQUESTMAPPING_IDENTIFIER_AS_UUID)
public class ItemtemplateRestController {

    @Autowired
    private Utils utils;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRestRepository itemRestRepository;

    /**
     * This method gets a template Item based on its uuid
     *
     * Example:
     * <pre>
     * {@code
     * curl http://<dspace.server.url>/api/core/itemtemplates/cb760455-837a-4159-bd12-dcdfafcadc78
     *  -XGET \
     *  -H 'Authorization: Bearer eyJhbGciOiJI...'
     * }
     * </pre>
     * @param request
     * @param uuid      A UUID of a template item
     * @return          The template item corresponding to the UUID above
     */
    @PreAuthorize("hasPermission(#uuid, 'COLLECTION', 'READ')")
    @RequestMapping(method = RequestMethod.GET)
    public ItemResource getTemplateItem(HttpServletRequest request, @PathVariable UUID uuid) {

        Context context = ContextUtil.obtainContext(request);
        ItemRest templateItem = itemRestRepository.findOne(context, uuid);

        if (templateItem == null) {
            throw new ResourceNotFoundException("Item with id: " + uuid + " not found");
        }

        if (templateItem.getTemplateItemOf() == null) {
            throw new ResourceNotFoundException("The item with id " + uuid + " is not a template item");
        }

        return new ItemResource(templateItem, utils);
    }

    /**
     * This method modifies installed template items
     *
     * Example:
     * <pre>
     * {@code
     * curl http://<dspace.server.url>/api/core/itemtemplates/cb760455-837a-4159-bd12-dcdfafcadc78
     *  -XPATCH -H 'Content-Type: Content-Type:application/json' \
     *  -H 'Authorization: Bearer eyJhbGciOiJI...' \
     *  --data '[
     *        {
     *          "op": "add",
     *          "path": "/metadata/dc.description",
     *          "value": [ { "value": "Some other first description" } ]
     *        }
     *      ]'
     * }
     * </pre>
     * @param request
     * @param uuid          The UUID of the template item to be modified
     * @param jsonNode      The data as shown above
     * @return              The modified item
     * @throws SQLException
     * @throws AuthorizeException
     */
    @PreAuthorize("hasPermission(#uuid, 'ITEM', 'WRITE')")
    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<ResourceSupport> replaceTemplateItem(HttpServletRequest request, @PathVariable UUID uuid,
                                                               @RequestBody(required = true) JsonNode jsonNode)
        throws SQLException, AuthorizeException {

        Context context = ContextUtil.obtainContext(request);
        Item item = getTemplateItem(context, uuid);
        ItemRest templateItem = itemRestRepository.patchTemplateItem(item, jsonNode);
        context.commit();

        return ControllerUtils.toResponseEntity(HttpStatus.OK, null,
            new ItemResource(templateItem, utils));
    }

    /**
     * This method deletes a template item from a collection.
     *
     * Example:
     * <pre>
     * {@code
     * curl http://<dspace.server.url>/api/core/itemtemplates/cb760455-837a-4159-bd12-dcdfafcadc78
     *  -XDELETE \
     *  -H 'Authorization: Bearer eyJhbGciOiJI...'
     * }
     * </pre>
     * @param request
     * @param uuid
     * @return          Status code 204 is returned if the deletion was successful
     * @throws SQLException
     * @throws AuthorizeException
     * @throws IOException
     */
    @PreAuthorize("hasPermission(#uuid, 'ITEM', 'DELETE')")
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<ResourceSupport> deleteTemplateItem(HttpServletRequest request, @PathVariable UUID uuid)
        throws SQLException, AuthorizeException, IOException {

        Context context = ContextUtil.obtainContext(request);
        Item item = getTemplateItem(context, uuid);
        itemRestRepository.removeTemplateItem(context, item);
        context.commit();

        return ControllerUtils.toEmptyResponse(HttpStatus.NO_CONTENT);
    }

    private Item getTemplateItem(Context context, UUID uuid) throws SQLException {
        Item item = itemService.find(context, uuid);
        if (item == null) {
            throw new ResourceNotFoundException(
                "The given uuid did not resolve to an item on the server: " + uuid);
        }
        if (item.getTemplateItemOf() == null) {
            throw new UnprocessableEntityException("The item with id " + uuid + " is not a template item");
        }
        return item;
    }
}
