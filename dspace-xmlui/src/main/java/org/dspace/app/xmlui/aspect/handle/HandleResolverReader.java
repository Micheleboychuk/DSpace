/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.handle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.google.gson.Gson;
import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.reading.AbstractReader;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.core.Context;
import org.dspace.handle.HandleManager;


/**
 *
 * @author Pascal-Nicolas Becker (p dot becker at tu hyphen berlin dot de)
 */
public class HandleResolverReader extends AbstractReader implements Recyclable {
    
    private static final Logger log = Logger.getLogger(HandleResolverReader.class);
    
    public static final String CONTENTTYPE = "application/json; charset=utf-8";
    
    private Request req;
    private Response resp;
    private String action;
    private String handle;
    private String prefix;
    
    
    public void setup(SourceResolver resolver, Map objectModel, String src,
            Parameters par) throws ProcessingException, SAXException,
            IOException
    {
        this.req = ObjectModelHelper.getRequest(objectModel);
        this.resp = ObjectModelHelper.getResponse(objectModel);
        this.action = par.getParameter("action", "listprefixes");
        this.handle = par.getParameter("handle", null);
        this.prefix = par.getParameter("prefix", null);

        super.setup(resolver, objectModel, src, par);
    }
    
    @Override
    public void generate() throws IOException, SAXException, ProcessingException {
        Request request = ObjectModelHelper.getRequest(objectModel);
        
        Context context = null;
        try {
            context = ContextUtil.obtainContext(objectModel);
        } catch (SQLException ex) {
            log.error(ex);
            throw new ProcessingException("Error in database conncetion.", ex);
        }
        
        Gson gson = new Gson();
        String jsonString = null;

        try {
            if (action.equals("resolve"))
            {
                if (null == handle)
                {
                    log.error("Shall resolve a handle but not handle parameter exists?");
                    throw new ProcessingException("Handle to resolve was not specified.");
                }
                jsonString = gson.toJson(new Url(HandleManager.resolveToURL(context, handle)));
            }
            else if (action.equals("listprefixes"))
            {
                String[] prefixes = { HandleManager.getPrefix() };
                jsonString = gson.toJson(new Prefixlist(prefixes));
            }
            else if (action.equals("listhandles"))
            {
                if (null == prefix || prefix.isEmpty())
                {
                    log.error("Shall list all handles but not prefix parameter exists?");
                    throw new ProcessingException("Can't determine prefix.");
                }

                List<String> handlelist = HandleManager.getHandlesForPrefix(context, prefix);
                String[] handles = handlelist.toArray(new String[handlelist.size()]);
                jsonString = gson.toJson(new Handlelist(handles));
            }
        } catch (SQLException e) {
            log.error("SQLException: ", e);
            return;
        }
        
        try {
            ObjectModelHelper.getResponse(objectModel).setHeader("Content-Type", CONTENTTYPE);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonString.getBytes("UTF-8"));
            IOUtils.copy(inputStream, out);
            out.flush();
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }
    
    public void recycle() {
        this.req = null;
        this.resp = null;
        this.action = null;
        this.handle = null;
        this.prefix = null;
        super.recycle();
    }

    private class Handlelist
    {
        String[] handles;
        Handlelist(String[] handles) {
            this.handles = handles;
        }
    }
    
    private class Prefixlist
    {
        String[] prefixes;
        Prefixlist(String[] prefixes)
        {
            this.prefixes = prefixes;
        }
    }
    
    private class Url
    {
        String url;
        Url(String url)
        {
            this.url = url;
        }
    }
    
}