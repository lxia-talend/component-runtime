/**
 * Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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
package org.talend.sdk.component.studio.metadata.node;

import java.io.ByteArrayInputStream;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.IImage;
import org.talend.repository.model.RepositoryNode;
import org.talend.sdk.component.server.front.model.ConfigTypeNode;
import org.talend.sdk.component.studio.Lookups;
import org.talend.sdk.component.studio.util.TaCoKitConst;

/**
 * DOC cmeng class global comment. Detailled comment
 */
public class TaCoKitFamilyRepositoryNode extends AbsTaCoKitRepositoryNode {

    public TaCoKitFamilyRepositoryNode(final RepositoryNode parent, final String label, final IImage icon,
            final ConfigTypeNode configTypeNode) {
        super(parent, label, icon, configTypeNode);
        setImage(getTaCoKitImage(configTypeNode));
    }

    public static Image getTaCoKitImage(final ConfigTypeNode configTypeNode) {
        try {
            ImageRegistry imageRegistry = JFaceResources.getImageRegistry();

            String id = configTypeNode.getId();
            String imageKey = TaCoKitConst.BUNDLE_ID + "/TaCoKit/Family/" + id; //$NON-NLS-1$

            Image image = imageRegistry.get(imageKey);
            if (image == null) {
                try {
                    image = getFamilyImage(id);
                    imageRegistry.put(imageKey, image);
                } catch (Exception e) {
                    ExceptionHandler.process(e);
                }
            }
            if (image != null) {
                return image;
            }
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
        return null;
    }

    private static Image getFamilyImage(final String id) throws Exception {
        byte[] icon = Lookups.client().v1().component().familyIcon(id);
        if (icon != null && 0 < icon.length) {
            ByteArrayInputStream inputStream = null;
            try {
                inputStream = new ByteArrayInputStream(icon);
                return new Image(Display.getDefault(), inputStream);
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    ExceptionHandler.process(e);
                }
            }
        }
        return null;
    }

}