package Presentar.MainViews;

public class ControllerMainView {

    private MainView view;

    public ControllerMainView(MainView view) {
        this.view = view;
    }

    public void recargarReservas() {
        view.recargarReservas();
    }
}