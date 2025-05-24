<?php
require_once "db.inc.php";
global $conn;

header("Content-Type: application/json");

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if (isset($_GET['id'])) {
        $id = $_GET['id'];
        $stmt = $conn->prepare("SELECT * FROM schedule WHERE schedule_id = ?");
        $stmt->bind_param("i", $id);
        $stmt->execute();
        $result = $stmt->get_result();
        echo json_encode($result->fetch_assoc());
    } else {
        $result = $conn->query("SELECT * FROM schedule");
        $schedules = [];
        while ($row = $result->fetch_assoc()) {
            $schedules[] = $row;
        }
        echo json_encode($schedules);
    }

} elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $year = $_POST['year'];
    $semester = $_POST['semester'];

    $stmt = $conn->prepare("INSERT INTO schedule (year, semester) VALUES (?, ?)");
    $stmt->bind_param("is", $year, $semester);

    if ($stmt->execute()) {
        echo json_encode(["success" => true, "id" => $conn->insert_id]);
    } else {
        echo json_encode(["success" => false, "error" => $stmt->error]);
    }

} elseif ($_SERVER['REQUEST_METHOD'] === 'PUT') {
    parse_str(file_get_contents("php://input"), $_PUT);
    $id = $_PUT['id'];
    $year = $_PUT['year'];
    $semester = $_PUT['semester'];

    $stmt = $conn->prepare("UPDATE schedule SET year = ?, semester = ? WHERE schedule_id = ?");
    $stmt->bind_param("isi", $year, $semester, $id);

    if ($stmt->execute()) {
        echo json_encode(["success" => true]);
    } else {
        echo json_encode(["success" => false, "error" => $stmt->error]);
    }

} elseif ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
    parse_str(file_get_contents("php://input"), $_DELETE);
    $id = $_DELETE['id'];

    $stmt = $conn->prepare("DELETE FROM schedule WHERE schedule_id = ?");
    $stmt->bind_param("i", $id);

    if ($stmt->execute()) {
        echo json_encode(["success" => true]);
    } else {
        echo json_encode(["success" => false, "error" => $stmt->error]);
    }
}