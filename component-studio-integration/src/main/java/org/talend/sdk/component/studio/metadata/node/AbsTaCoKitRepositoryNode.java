/**
 * Copyright (C) 2006-2017 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.talend.sdk.component.studio.metadata.node;

import org.eclipse.swt.graphics.Image;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.sdk.component.server.front.model.ConfigTypeNode;
import org.talend.sdk.component.studio.util.TaCoKitUtil;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * DOC cmeng class global comment. Detailled comment
 */
@Getter
@Setter
@EqualsAndHashCode(of = "configTypeNode", callSuper = true)
public abstract class AbsTaCoKitRepositoryNode extends RepositoryNode implements ITaCoKitRepositoryNode {

    private ITaCoKitRepositoryNode parentTaCoKitNode;

    private ConfigTypeNode configTypeNode;

    private Image image;

    public AbsTaCoKitRepositoryNode(final IRepositoryViewObject repViewObject, final RepositoryNode parent,
            final ITaCoKitRepositoryNode parentTaCoKitNode, final String label, final ConfigTypeNode configTypeNode)
            throws Exception {
        super(repViewObject, parent, IRepositoryNode.ENodeType.SYSTEM_FOLDER);
        this.parentTaCoKitNode = parentTaCoKitNode;
        this.configTypeNode = configTypeNode;
        this.setProperties(EProperties.LABEL, label);
        this.setProperties(EProperties.CONTENT_TYPE, TaCoKitUtil.getOrCreateERepositoryObjectType(configTypeNode));
    }

    public void setContentType(final ERepositoryObjectType contentType) {
        this.setProperties(EProperties.CONTENT_TYPE, contentType);
    }

    @Override
    public boolean isLeafNode() {
        return false;
    }

    @Override
    public boolean isFolderNode() {
        return false;
    }

    @Override
    public boolean isFamilyNode() {
        return false;
    }

    @Override
    public boolean isConfigNode() {
        return false;
    }

}