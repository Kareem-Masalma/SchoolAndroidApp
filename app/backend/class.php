<?php
require_once "db.inc.php";
global $conn;

// Headers
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Method & Input
$method = $_SERVER['REQUEST_METHOD'];
$input = json_decode(file_get_contents("php://input"), true);

try {
    if ($method === 'GET') {
        if (isset($_GET['class_id'])) {
            $id = (int)$_GET['class_id'];
            $stmt = $conn->prepare("SELECT * FROM class WHERE class_id = ?");
            $stmt->bind_param("i", $id);
            $stmt->execute();
            $result = $stmt->get_result()->fetch_assoc();

            if (!$result) {
                http_response_code(404);
                echo json_encode(['error' => 'Class not found']);
            } else {
                echo json_encode($result);
            }
        } else if (isset($_GET['user_id'])) {
            $user_id = (int)$_GET['user_id'];
            $stmt = $conn->prepare("SELECT c.class_id, c.class_name, c.class_manager, c.schedule_id  FROM teacher t, schedule s, schedule_subject ss, subject sb, class c WHERE t.schedule_id = s.schedule_id AND s.schedule_id = ss.schedule_id AND ss.subject_id = sb.subject_id AND sb.class_id = c.class_id AND t.user_id = ?;");
            $stmt->bind_param("i", $user_id);
            $stmt->execute();
            $result = $stmt->get_result();
            $rows = $result ? $result->fetch_all(MYSQLI_ASSOC) : [];
            echo json_encode($rows);
        } else {
            $result = $conn->query("SELECT * FROM class");
            $rows = $result ? $result->fetch_all(MYSQLI_ASSOC) : [];
            echo json_encode($rows);
        }

    } elseif ($method === 'POST') {
        foreach (['class_name', 'class_manager', 'schedule_id'] as $key) {
            if (empty($input[$key])) {
                throw new Exception("Missing field: $key");
            }
        }

        $stmt = $conn->prepare("INSERT INTO class (class_name, class_manager, schedule_id) VALUES (?, ?, ?)");
        $stmt->bind_param("sii", $input['class_name'], $input['class_manager'], $input['schedule_id']);
        $stmt->execute();

        echo json_encode([
            'success' => true,
            'message' => 'Class added successfully',
            'class_id' => $conn->insert_id
        ]);

    } elseif ($method === 'PUT') {
        if (empty($input['class_id'])) {
            throw new Exception("Missing class_id");
        }

        $stmt = $conn->prepare("
            UPDATE class
            SET class_name = ?, class_manager = ?, schedule_id = ?
            WHERE class_id = ?
        ");
        $stmt->bind_param(
            "siii",
            $input['class_name'],
            $input['class_manager'],
            $input['schedule_id'],
            $input['class_id']
        );
        $stmt->execute();

        echo json_encode([
            'success' => true,
            'message' => 'Class updated successfully'
        ]);

    } elseif ($method === 'DELETE') {
        if (empty($_GET['class_id'])) {
            throw new Exception("Missing class_id");
        }
        $id = (int)$_GET['class_id'];

        $stmt = $conn->prepare("DELETE FROM class WHERE class_id = ?");
        $stmt->bind_param("i", $id);
        $stmt->execute();

        echo json_encode([
            'success' => true,
            'message' => 'Class deleted successfully'
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
?>
