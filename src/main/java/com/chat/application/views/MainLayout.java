package com.chat.application.views;


import com.chat.application.components.appnav.AppNav;
import com.chat.application.components.appnav.AppNavItem;
import com.chat.application.constant.ImageConst;
import com.chat.application.util.ImageUtil;
import com.chat.application.util.RequestUtil;
import com.chat.application.views.chat.*;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.coyote.Request;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");
        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        Span span = new Span(ImageUtil.getAvatar(ImageConst.LOGIN)
                ,new Html("<span>&nbsp;&nbsp;&nbsp;</span>")
                ,new Text(RequestUtil.getRequestIp()));
        span.getStyle().set("position","absolute");
        span.getStyle().set("display","flex");
        span.getStyle().set("align-items","center");
        span.getStyle().set("right","1%");
        span.getStyle().set("font-weight","bold");
        addToNavbar(true, toggle, viewTitle,span);
    }

    private void addDrawerContent() {
        H1 appName = new H1("AI聊天室");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);
        header.getElement().setAttribute("theme",Lumo.DARK);
        Scroller scroller = new Scroller(createNavigation());
        scroller.getElement().setAttribute("theme",Lumo.DARK);
        addToDrawer(header, scroller ,createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();
        nav.addItem(new AppNavItem("聊天室", DefaultAbstractChatRoom.class, LineAwesomeIcon.GLOBE_SOLID.create()));
        nav.addItem(new AppNavItem("聊天室2号", PhilAbstractChatRoom.class, LineAwesomeIcon.HISTORY_SOLID.create()));
        return nav;
    }

    private Footer createFooter() {
        //Footer footer = new Footer(new Text("回答仅供参考，不负法律责任"));
        Footer footer = new Footer(new Text(""));
        footer.getElement().setAttribute("theme",Lumo.DARK);
        return footer;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());

    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
