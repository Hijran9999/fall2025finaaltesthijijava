import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;

public class Main extends Application {

    // Validation patterns
    private static final String NAME_PATTERN = "^[A-Za-z ]{1,50}$";
    private static final String CONTACT_PATTERN = "^\\d{10}$";
    private static final String SALARY_PATTERN = "^\\d{1,8}\\.\\d{2}$"; // exactly two decimals, up to 8 digits before decimal

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Employment Application - Final_Summer2025_Hiji");
        // GridPane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(18));
        grid.setHgap(12);
        grid.setVgap(12);

        // Top: company image + title (image placed left)
        HBox topBox = new HBox(12);
        topBox.setAlignment(Pos.CENTER_LEFT);
        ImageView logo = loadLogo();
        if (logo != null) {
            logo.setFitHeight(60);
            logo.setPreserveRatio(true);
            topBox.getChildren().add(logo);
        }
        Label header = new Label("Employment Application");
        header.setStyle("-fx-font-size:20px; -fx-font-weight: bold;");
        topBox.getChildren().add(header);
        grid.add(topBox, 0, 0, 2, 1);

        // Personal Information
        Label lblFull = new Label("Full Name:");
        TextField tfFull = new TextField();
        tfFull.setPromptText("Only letters and spaces, max 50");
        grid.add(lblFull, 0, 1);
        grid.add(tfFull, 1, 1);

        Label lblAddress = new Label("Current Address:");
        TextField tfAddress = new TextField();
        grid.add(lblAddress, 0, 2);
        grid.add(tfAddress, 1, 2);

        Label lblContact = new Label("Contact Number:");
        TextField tfContact = new TextField();
        tfContact.setPromptText("10 digits");
        grid.add(lblContact, 0, 3);
        grid.add(tfContact, 1, 3);

        Label lblEmail = new Label("Email Address:");
        TextField tfEmail = new TextField();
        grid.add(lblEmail, 0, 4);
        grid.add(tfEmail, 1, 4);

        Label lblEducation = new Label("Highest Education:");
        ComboBox<String> cbEducation = new ComboBox<>();
        cbEducation.getItems().addAll("Masters", "Bachelors", "College Diploma");
        cbEducation.setPromptText("Select");
        grid.add(lblEducation, 0, 5);
        grid.add(cbEducation, 1, 5);

        // Employment Eligibility
        Separator sep = new Separator();
        sep.setPadding(new Insets(8,0,8,0));
        grid.add(sep, 0, 6, 2, 1);

        Label lblDate = new Label("Date Available:");
        DatePicker dpDate = new DatePicker();
        grid.add(lblDate, 0, 7);
        grid.add(dpDate, 1, 7);

        Label lblPosition = new Label("Desired Position:");
        TextField tfPosition = new TextField();
        grid.add(lblPosition, 0, 8);
        grid.add(tfPosition, 1, 8);

        Label lblSalary = new Label("Desired Salary:");
        TextField tfSalary = new TextField();
        tfSalary.setPromptText("12345678.50 (max 8 digits before decimal, two decimals)");
        grid.add(lblSalary, 0, 9);
        grid.add(tfSalary, 1, 9);

        Label lblAuthorized = new Label("Legally authorized to work?");
        ToggleGroup authGroup = new ToggleGroup();
        RadioButton rbAuthYes = new RadioButton("Yes");
        RadioButton rbAuthNo = new RadioButton("No");
        rbAuthYes.setToggleGroup(authGroup);
        rbAuthNo.setToggleGroup(authGroup);
        HBox authBox = new HBox(8, rbAuthYes, rbAuthNo);
        grid.add(lblAuthorized, 0, 10);
        grid.add(authBox, 1, 10);

        Label lblRelatives = new Label("Do you have relatives at our company?");
        ToggleGroup relGroup = new ToggleGroup();
        RadioButton rbRelYes = new RadioButton("Yes");
        RadioButton rbRelNo = new RadioButton("No");
        rbRelYes.setToggleGroup(relGroup);
        rbRelNo.setToggleGroup(relGroup);
        HBox relBox = new HBox(8, rbRelYes, rbRelNo);
        grid.add(lblRelatives, 0, 11);
        grid.add(relBox, 1, 11);

        Label lblRelExplain = new Label("If yes, explain:");
        TextField tfRelExplain = new TextField();
        grid.add(lblRelExplain, 0, 12);
        grid.add(tfRelExplain, 1, 12);

        // Replace signature box with Submit button
        Button btnSubmit = new Button("Submit");
        btnSubmit.setPrefWidth(120);
        HBox submitBox = new HBox(btnSubmit);
        submitBox.setAlignment(Pos.CENTER_RIGHT);
        grid.add(submitBox, 1, 13);

        // Status area / feedback
        Label lblStatus = new Label();
        grid.add(lblStatus, 0, 14, 2, 1);

        // Submit action
        btnSubmit.setOnAction(e -> {
            lblStatus.setText("");
            String fullName = tfFull.getText() == null ? "" : tfFull.getText().trim();
            String address = tfAddress.getText() == null ? "" : tfAddress.getText().trim();
            String contact = tfContact.getText() == null ? "" : tfContact.getText().trim();
            String email = tfEmail.getText() == null ? "" : tfEmail.getText().trim();
            String education = cbEducation.getValue();
            java.time.LocalDate dateAvailable = dpDate.getValue();
            String position = tfPosition.getText() == null ? "" : tfPosition.getText().trim();
            String salaryStr = tfSalary.getText() == null ? "" : tfSalary.getText().trim();
            Toggle authToggle = authGroup.getSelectedToggle();
            Toggle relToggle = relGroup.getSelectedToggle();
            String relExplain = tfRelExplain.getText() == null ? "" : tfRelExplain.getText().trim();

            // Validate
            StringBuilder errors = new StringBuilder();
            if (!fullName.matches(NAME_PATTERN)) {
                errors.append("- Full name must be letters/spaces only and up to 50 chars.\n");
            }
            if (!contact.matches(CONTACT_PATTERN)) {
                errors.append("- Contact number must be exactly 10 digits.\n");
            }
            if (education == null) {
                errors.append("- Highest education must be selected.\n");
            }
            if (dateAvailable == null) {
                errors.append("- Date available must be chosen.\n");
            }
            if (!salaryStr.matches(SALARY_PATTERN)) {
                errors.append("- Salary must be up to 8 digits before decimal and have exactly two decimals (e.g. 12345678.50).\n");
            }
            if (authToggle == null) {
                errors.append("- Answer whether you are authorized to work.\n");
            }
            if (relToggle == null) {
                errors.append("- Answer whether you have relatives working for the company.\n");
            }

            if (errors.length() > 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation errors");
                alert.setHeaderText("Please fix the following:");
                alert.setContentText(errors.toString());
                alert.showAndWait();
                return;
            }

            boolean authorized = authToggle == rbAuthYes;
            boolean relatives = relToggle == rbRelYes;

            // Save to DB
            try (Connection conn = DBUtil.getConnection()) {
                conn.setAutoCommit(false);

                // Insert into ApplicantTable
                String insertApplicant = "INSERT INTO ApplicantTable (full_name, contact_number, email, current_address, highest_education) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pst = conn.prepareStatement(insertApplicant, Statement.RETURN_GENERATED_KEYS)) {
                    pst.setString(1, fullName);
                    pst.setString(2, contact);
                    pst.setString(3, email);
                    pst.setString(4, address);
                    pst.setString(5, education);
                    pst.executeUpdate();

                    ResultSet keys = pst.getGeneratedKeys();
                    if (keys.next()) {
                        int applicantId = keys.getInt(1);

                        // Insert into EmploymentTable
                        String insertEmp = "INSERT INTO EmploymentTable (applicant_id, date_available, desired_position, desired_salary, authorized_to_work, relatives_work_for_company, relatives_explanation) VALUES (?, ?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement pst2 = conn.prepareStatement(insertEmp)) {
                            pst2.setInt(1, applicantId);
                            pst2.setDate(2, Date.valueOf(dateAvailable));
                            pst2.setString(3, position);
                            pst2.setBigDecimal(4, new BigDecimal(salaryStr));
                            pst2.setBoolean(5, authorized);
                            pst2.setBoolean(6, relatives);
                            pst2.setString(7, relExplain);
                            pst2.executeUpdate();
                        }
                    } else {
                        throw new SQLException("Failed to retrieve applicant id.");
                    }
                }

                conn.commit();
                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("Success");
                ok.setHeaderText(null);
                ok.setContentText("Application submitted and stored successfully.");
                ok.showAndWait();

                // clear form
                tfFull.clear(); tfAddress.clear(); tfContact.clear(); tfEmail.clear();
                cbEducation.setValue(null); dpDate.setValue(null);
                tfPosition.clear(); tfSalary.clear();
                authGroup.selectToggle(null); relGroup.selectToggle(null); tfRelExplain.clear();

            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Database Error");
                alert.setHeaderText("Could not save application");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        Scene scene = new Scene(grid, 800, 620);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ImageView loadLogo() {
        // Load image from resources (place in resources folder: /images/company_logo.png)
        try {
            InputStream is = getClass().getResourceAsStream("/images/company_logo.png");
            if (is == null) {
                // fallback: try container image path if developer provided it
                return null;
            }
            Image img = new Image(is);
            ImageView iv = new ImageView(img);
            return iv;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
