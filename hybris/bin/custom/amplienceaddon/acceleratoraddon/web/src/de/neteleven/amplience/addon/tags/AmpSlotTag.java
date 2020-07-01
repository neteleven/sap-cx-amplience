/*
 * Copyright (c) 2020. neteleven GmbH (https://www.neteleven.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.neteleven.amplience.addon.tags;


import de.hybris.platform.acceleratorcms.data.CmsPageRequestContextData;
import de.hybris.platform.acceleratorcms.services.CMSPageContextService;
import de.hybris.platform.acceleratorservices.util.SpringHelper;
import de.neteleven.amplience.addon.AmplienceContentFacade;
import de.neteleven.amplience.data.AmpSlotData;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;


/**
 * Tag that prints html or optional json for amplience slot.
 * <p>
 * Use context information (position, context, lookup and optional published)
 * <p>
 * if published is set, then use it
 * if published is not set, then check if in liveEdit/preview mode and set it to false otherwise to true
 */
public class AmpSlotTag extends TagSupport {

    private static final Logger LOG = Logger.getLogger(AmpSlotTag.class.getName());

    private String key;
    private String position;
    private String context;
    private String lookup;
    private Boolean published;
    private Boolean json;
    private AmplienceContentFacade amplienceContentFacade;

    public void setKey(final String key) {
        this.key = key;
    }

    public void setPosition(final String position) {
        this.position = position;
    }

    public void setContext(final String context) {
        this.context = context;
    }

    public void setLookup(final String lookup) {
        this.lookup = lookup;
    }

    public void setPublished(final Boolean published) {
        this.published = published;
    }

    public void setJson(final Boolean json) {
        this.json = json;
    }

    @Override
    public int doStartTag() {
        if (amplienceContentFacade == null) {
            amplienceContentFacade = lookupAmplienceContentFacade();
        }

        final AmpSlotData ampSlotData;

        if (StringUtils.isNotEmpty(key)) {
            ampSlotData = amplienceContentFacade.getSlotByKey(key, (published != null) ? published : !isPreviewOrLiveEditEnabled());
        } else {
            ampSlotData = amplienceContentFacade.getSlot(position, context, lookup, (published != null) ? published : !isPreviewOrLiveEditEnabled());
        }

        if (ampSlotData != null) {
            final JspWriter jspWriter = pageContext.getOut();

            try {
                boolean printJson = false;
                if (json != null) {
                    printJson = json;
                }
                jspWriter.println((printJson) ? ampSlotData.getJson() : ampSlotData.getHtml());
            } catch (final IOException ex) {
                LOG.error(ex.getMessage());
            }
            return SKIP_BODY;
        }
        // fallback: evaluate the body
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }


    protected AmplienceContentFacade lookupAmplienceContentFacade() {
        return SpringHelper.getSpringBean(pageContext.getRequest(), "defaultAmplienceContentFacade",
                AmplienceContentFacade.class, true);
    }

    protected boolean isPreviewOrLiveEditEnabled() {
        final CmsPageRequestContextData cmsPageRequestContextData = SpringHelper
                .getSpringBean(pageContext.getRequest(), "cmsPageContextService", CMSPageContextService.class, true)
                .getCmsPageRequestContextData(pageContext.getRequest());
        return cmsPageRequestContextData.isLiveEdit() || cmsPageRequestContextData.isPreview();
    }

}
