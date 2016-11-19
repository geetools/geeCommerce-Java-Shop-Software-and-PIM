package com.geecommerce.guiwidgets.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.Id;
import com.geecommerce.guiwidgets.enums.ContentNodeType;
import com.geecommerce.guiwidgets.enums.ContentType;

import java.util.List;
import java.util.Map;

public interface ContentNode extends Model {

    public Map<String, Object> getParameterValues();

    public ContentNode setParameterValues(Map<String, Object> parameterValuess);

    public ContentNodeType getType();

    public ContentNode setType(ContentNodeType type);

    public String getKey();

    public ContentNode setKey(String key);

    public String getContent();

    public ContentNode setContent(String content);

    public String getPreview();

    public ContentNode setPreview(String preview);

    public String getWidget();

    public ContentNode setWidget(String widget);

    public String getNodeId();

    public ContentNode setNodeId(String nodeId);

  /*  public String getType2();

    public ContentNode setType2(String type2);



    public String getCss();

    public ContentNode setCss(String css);

    public List<ContentNode> getNodes();

    public ContentNode setNodes(List<ContentNode> nodes);

*/

    static final class Col {
        public static final String PARAMETER_VALUES = "param_values";
        public static final String WIDGET = "widget";
        public static final String NODE_ID = "node_id";
        public static final String CONTENT = "content";
        public static final String TYPE = "type";
        public static final String KEY = "key";
        public static final String PREVIEW = "preview";
/*

	public static final String TYPE2 = "type2";



	public static final String CSS = "css";
	public static final String NODES = "nodes";
	*/
    }

}
