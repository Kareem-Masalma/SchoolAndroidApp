<?php
require_once "db.inc.php";
global $conn;

header("Content-Type: application/json; charset=utf-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

$method = $_SERVER['REQUEST_METHOD'];
$input = json_decode(file_get_contents("php://input"), true);

try {
    if ($method === 'GET') {
        if (isset($_GET['schedule_id'])) {
            $id = (int)$_GET['schedule_id'];
            $stmt = $conn->prepare("SELECT * FROM schedule WHERE schedule_id = ?");
            $stmt->bind_param("i", $id);
            $stmt->execute();
            $row = $stmt->get_result()->fetch_assoc();

            if (!$row) {
                http_response_code(404);
                echo json_encode(["error" => "Schedule not found"]);
            } else {
                echo json_encode($row);
            }
        } else {
            $result = $conn->query("SELECT * FROM schedule");
            $rows = $result ? $result->fetch_all(MYSQLI_ASSOC) : [];
            echo json_encode($rows);
        }

    } elseif ($method === 'POST') {
        if (empty($input['year']) || empty($input['semester'])) {
            throw new Exception("Missing year or semester");
        }

        $year = (int)$input['year'];
        $semester = $input['semester'];

        $stmt = $conn->prepare("INSERT INTO schedule (year, semester) VALUES (?, ?)");
        $stmt->bind_param("is", $year, $semester);
        $stmt->execute();

        echo json_encode([
            'success' => true,
            'message' => 'Schedule created',
            'id' => $conn->insert_id
        ], JSON_UNESCAPED_UNICODE);

    } elseif ($method === 'PUT') {
        if (empty($input['id']) || empty($input['year']) || empty($input['semester'])) {
            throw new Exception("Missing id, year or semester");
        }

        $id = (int)$input['id'];
        $year = (int)$input['year'];
        $semester = $input['semester'];

        $stmt = $conn->prepare("UPDATE schedule SET year = ?, semester = ? WHERE schedule_id = ?");
        $stmt->bind_param("isi", $year, $semester, $id);
        $stmt->execute();

        echo json_encode([
            'success' => true,
            'message' => 'Schedule updated'
        ]);

    } elseif ($method === 'DELETE') {
        if (empty($_GET['schedule_id'])) {
            throw new Exception("Missing schedule_id");
        }
        $id = (int)$_GET['schedule_id'];

        $stmt = $conn->prepare("DELETE FROM schedule WHERE schedule_id = ?");
        $stmt->bind_param("i", $id);
        $stmt->execute();

        echo json_encode([
            'success' => true,
            'message' => 'Schedule deleted'
        ]);

    } else {
        http_response_code(405);
        echo json_encode(['error' => 'Method not allowed']);
    }

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'error' => $e->getMessage()
    ]);
}
