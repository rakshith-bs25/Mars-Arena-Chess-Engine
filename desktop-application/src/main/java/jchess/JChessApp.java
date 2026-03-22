package jchess;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

public class JChessApp extends SingleFrameApplication {
    static JChessView jcv;

    @Override
    protected void startup() {
        jcv = new JChessView(this);
        show(jcv);
    }

    @Override
    protected void configureWindow(java.awt.Window root) { }

    public static JChessApp getApplication() {
        return Application.getInstance(JChessApp.class);
    }

    public static void main(String[] args) {
        Application.launch(JChessApp.class, args);
    }
}
