<?php
global $conn;
require_once "db.inc.php";

if ($_SERVER["REQUEST_METHOD"] == "GET") {
    if (isset($_GET["id"])) {
        $id = $_GET["id"];
        $sql = "SELECT * FROM teacher t, users u WHERE u.id = ? AND t.teacher_id = u.id";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("i", $id);
        $stmt->execute();
        $result = $stmt->get_result();
        echo json_encode($result->fetch_assoc() ?: ["error" => "Teacher not found"]);
    } else {
        $sql = "SELECT * FROM teacher t, users u WHERE t.teacher_id = u.id";
        $result = $conn->query($sql);
        $teachers = [];
        while ($row = $result->fetch_assoc()) {
            $teachers[] = $row;
        }
        echo json_encode($teachers);
    }
}

else if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $first_name = $_POST["first_name"];
    $last_name = $_POST["last_name"];
    $birth_date = $_POST["birth_date"];
    $address = $_POST["address"];
    $phone = $_POST["phone"];
    $role = $_POST["role"];
    $speciality = $_POST["speciality"];

    $user_stmt = $conn->prepare("INSERT INTO users (first_name, last_name, birth_date, address, phone, role) VALUES (?, ?, ?, ?, ?, ?)");
    $user_stmt->bind_param("ssssss", $first_name, $last_name, $birth_date, $address, $phone, $role);

    if ($user_stmt->execute()) {
        $user_id = $conn->insert_id;
        $teacher_stmt = $conn->prepare("INSERT INTO teacher (teacher_id, department) VALUES (?, ?)");
        $teacher_stmt->bind_param("is", $user_id, $speciality);
        echo json_encode($teacher_stmt->execute()
            ? ["success" => true, "message" => "Teacher added successfully"]
            : ["success" => false, "error" => "Failed to insert into teacher table"]);
        $teacher_stmt->close();
    } else {
        echo json_encode(["success" => false, "error" => "Failed to insert into user table"]);
    }
}

else if ($_SERVER["REQUEST_METHOD"] == "PUT") {
    parse_str(file_get_contents("php://input"), $_PUT);
    $id = $_PUT["id"];
    $first_name = $_PUT["first_name"];
    $last_name = $_PUT["last_name"];
    $birth_date = $_PUT["birth_date"];
    $address = $_PUT["address"];
    $phone = $_PUT["phone"];
    $role = $_PUT["role"];
    $speciality = $_PUT["speciality"];

    $user_stmt = $conn->prepare("UPDATE users SET first_name=?, last_name=?, birth_date=?, address=?, phone=?, role=? WHERE id=?");
    $user_stmt->bind_param("ssssssi", $first_name, $last_name, $birth_date, $address, $phone, $role, $id);

    if ($user_stmt->execute()) {
        if (!empty($speciality)) {
            $teacher_stmt = $conn->prepare("UPDATE teacher SET department=? WHERE teacher_id=?");
            $teacher_stmt->bind_param("si", $speciality, $id);
            echo json_encode($teacher_stmt->execute()
                ? ["success" => true, "message" => "Teacher updated successfully"]
                : ["success" => false, "error" => "Failed to update teacher table"]);
            $teacher_stmt->close();
        } else {
            echo json_encode(["success" => true, "message" => "Teacher updated successfully"]);
        }
    } else {
        echo json_encode(["success" => false, "error" => "Failed to update user table"]);
    }
}

else if ($_SERVER["REQUEST_METHOD"] == "DELETE") {
    parse_str(file_get_contents("php://input"), $_DELETE);
    $id = $_DELETE["id"];

    $stmt1 = $conn->prepare("DELETE FROM teacher WHERE teacher_id=?");
    $stmt1->bind_param("i", $id);
    $stmt2 = $conn->prepare("DELETE FROM users WHERE id=?");
    $stmt2->bind_param("i", $id);

    echo json_encode(($stmt1->execute() && $stmt2->execute())
        ? ["success" => true, "message" => "Teacher deleted successfully"]
        : ["success" => false, "error" => "Failed to delete teacher"]);
}
?>
