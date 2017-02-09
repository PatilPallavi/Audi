<%@page session="false"%>
<%@ page import="com.day.cq.wcm.foundation.External, 
                 com.day.cq.wcm.foundation.Placeholder" %>
<%@include file="/libs/foundation/global.jsp"%>
<div class="col-xs-12 col-md-6">
<%

    External external = new External(resource, currentPage, "spool", "external", "CFC__target");

    // draw placeholder for UI mode touch
    boolean isAuthoringUIModeTouch = Placeholder.isAuthoringUIModeTouch(slingRequest);
    if (isAuthoringUIModeTouch && !external.hasContent()) {
        %><%= Placeholder.getDefaultPlaceholder(slingRequest, component, "") %><%
    }

    String value = request.getParameter("CFC__target");
    if (value != null) {
        external.setTarget(value);
    }
//out.println("<div class="col-xs-12 col-md-6">");
    external.draw(slingRequest, slingResponse);
//out.println("</div>");
%>
</div>