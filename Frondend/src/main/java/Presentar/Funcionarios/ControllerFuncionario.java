package Presentar.Funcionarios;

import ClasesUsuario.Rol;
import ClasesUsuario.User;
//import Service.PDFService;
import logic.ServiceLocator;

import java.io.IOException;
import java.util.List;

public class ControllerFuncionario {

    private final ModelFuncionario model = new ModelFuncionario();

    public ControllerFuncionario() {
        cargarLista();
    }

    public ModelFuncionario getModel() {
        return model;
    }

    private void asegurarConexion() {
        try {
            ServiceLocator.conectar();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No fue posible conectar con el Backend.",
                    e
            );
        }
    }

    private void cargarLista() {
        asegurarConexion();

        model.setList(
                ServiceLocator.getService().buscarFuncionarios("", "")
        );
    }

    public List<User> buscar(String id, String nombre) {
        asegurarConexion();

        List<User> resultado = ServiceLocator.getService()
                .buscarFuncionarios(id, nombre);

        model.setList(resultado);

        return resultado;
    }

    public boolean guardar(String id, String nombre, String telefono) {
        if (!telefonoValido(telefono)) {
            return false;
        }

        asegurarConexion();

        User funcionario = new User(
                id,
                id,
                Rol.FUNCIONARIO,
                nombre,
                telefono
        );

        boolean resultado = ServiceLocator.getService()
                .guardarFuncionario(funcionario);

        if (resultado) {
            model.setCurrent(funcionario);
            cargarLista();
        }

        return resultado;
    }

    public boolean actualizar(String id, String nombre, String telefono) {
        if (!telefonoValido(telefono)) {
            return false;
        }

        asegurarConexion();

        User existente = ServiceLocator.getService()
                .buscarUsuarioPorId(id);

        if (existente == null
                || existente.getVarRol() != Rol.FUNCIONARIO) {
            return false;
        }

        User funcionario = new User(
                id,
                existente.getVarClave(),
                Rol.FUNCIONARIO,
                nombre,
                telefono
        );

        boolean resultado = ServiceLocator.getService()
                .actualizarFuncionario(funcionario);

        if (resultado) {
            model.setCurrent(funcionario);
            cargarLista();
        }

        return resultado;
    }

    public boolean eliminar(String id) {
        asegurarConexion();

        User existente = ServiceLocator.getService()
                .buscarUsuarioPorId(id);

        if (existente == null
                || existente.getVarRol() != Rol.FUNCIONARIO) {
            return false;
        }

        boolean resultado = ServiceLocator.getService()
                .eliminarFuncionario(id);

        if (resultado) {
            model.setCurrent(
                    new User("", "", Rol.FUNCIONARIO, "", "")
            );

            cargarLista();
        }

        return resultado;
    }

    public void limpiar() {
        model.setCurrent(
                new User("", "", Rol.FUNCIONARIO, "", "")
        );
    }

//    public void generarPDF() throws Exception {
//        PDFService.generarPDFFuncionarios(model.getList());
//    }

    private boolean telefonoValido(String telefono) {
        return telefono != null && telefono.matches("[0-9]+");
    }
}