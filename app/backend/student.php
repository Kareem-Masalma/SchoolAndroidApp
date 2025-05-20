<?php
global $conn;
require_once "db.inc.php";

if ($_SERVER["REQUEST_METHOD"] == "GET") {
    if (isset($_GET["id"])) {
        $id = $_GET["id"];
        $sql = "SELECT * FROM student s, user u  WHERE id = ? AND s.student_id = u.id";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("i", $id);
        $stmt->execute();
        $result = $stmt->get_result();
        if ($result->num_rows > 0) {
            $student = $result->fetch_assoc();
            echo json_encode($student);
        } else {
            echo json_encode(["error" => "Student not found"]);
        }
    } else {
        $sql = "SELECT * FROM student s, user u WHERE s.student_id = u.id";
        $result = $conn->query($sql);
        if ($result->num_rows > 0) {
            $students = [];
            while ($row = $result->fetch_assoc()) {
                $students[] = $row;
            }
            echo json_encode($students);
        } else {
            echo json_encode([]);
        }
    }
} else if ($_SERVER["REQUEST_METHOD"] == "POST") {

    $first_name = $_POST["first_name"];
    $last_name = $_POST["last_name"];
    $birth_date = $_POST["birth_date"];
    $address = $_POST["address"];
    $phone = $_POST["phone"];
    $role = $_POST["role"];
    $class_id = $_POST["class_id"];

    $user_stmt = $conn->prepare("INSERT INTO users (first_name, last_name, birth_date, address, phone, role) VALUES (?, ?, ?, ?, ?, ?)");
    $user_stmt->bind_param("ssssss", $first_name, $last_name, $birth_date, $address, $phone, $role);

    if ($user_stmt->execute()) {
        $user_id = $conn->insert_id;

        $student_stmt = $conn->prepare("INSERT INTO student (student_id, class_id) VALUES (?, ?)");
        $student_stmt->bind_param("ii", $user_id, $class_id);

        if ($student_stmt->execute()) {
            echo json_encode(["success" => true, "message" => "Student added successfully"]);
        } else {
            echo json_encode(["success" => false, "error" => "Failed to insert into student table"]);
        }

        $student_stmt->close();
    } else {
        echo json_encode(["success" => false, "error" => "Failed to insert into users table"]);
    }
} else if ($_SERVER["REQUEST_METHOD"] == "PUT") {
    parse_str(file_get_contents("php://input"), $_PUT);
    $id = $_PUT["id"];
    $first_name = $_PUT["first_name"];
    $last_name = $_PUT["last_name"];
    $birth_date = $_PUT["birth_date"];
    $address = $_PUT["address"];
    $phone = $_PUT["phone"];
    $role = $_PUT["role"];
    $class_id = $_PUT["class_id"];

    $user_stmt = $conn->prepare("UPDATE users SET first_name=?, last_name=?, birth_date=?, address=?, phone=?, role=? WHERE id=?");
    $user_stmt->bind_param("ssssssi", $first_name, $last_name, $birth_date, $address, $phone, $role, $id);

    if ($user_stmt->execute()) {
        if ($class_id) {
            $student_stmt = $conn->prepare("UPDATE student SET class_id=? WHERE student_id=?");
            $student_stmt->bind_param("ii", $class_id, $id);
            if ($student_stmt->execute()) {
                echo json_encode(["success" => true, "message" => "Student updated successfully"]);
            } else {
                echo json_encode(["success" => false, "error" => "Failed to update students table"]);
            }
            $student_stmt->close();
        } else {
            echo json_encode(["success" => true, "message" => "Student updated successfully"]);
        }
    } else {
        echo json_encode(["success" => false, "error" => "Failed to update users table"]);
    }
} else if ($_SERVER["REQUEST_METHOD"] == "DELETE") {
    parse_str(file_get_contents("php://input"), $_DELETE);
    $id = $_DELETE["id"];

    $stmt1 = $conn->prepare("DELETE FROM student WHERE student_id=?");
    $stmt1->bind_param("i", $id);

    $stmt2 = $conn->prepare("DELETE FROM users WHERE id=?");
    $stmt2->bind_param("i", $id);

    if ($stmt1->execute() && $stmt2->execute()) {
        echo json_encode(["success" => true, "message" => "Student deleted successfully"]);
    } else {
        echo json_encode(["success" => false, "error" => "Failed to delete student"]);
    }
}