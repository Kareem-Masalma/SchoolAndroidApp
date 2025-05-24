<?php
// attendance.php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

require_once "db.inc.php";

$method = $_SERVER['REQUEST_METHOD'];
$input  = json_decode(file_get_contents('php://input'), true);

try {
  if ($method === 'GET') {
    // GET all or single attendance by attendance_id
    if (isset($_GET['attendance_id'])) {
      $aid = (int)$_GET['attendance_id'];
      $stmt = $conn->prepare("
                SELECT attendance_id, date, class_id
                FROM attendance
                WHERE attendance_id = ?
            ");
      $stmt->bind_param("i", $aid);
      $stmt->execute();
      $row = $stmt->get_result()->fetch_assoc();
      if (!$row) {
        http_response_code(404);
        echo json_encode(['error' => 'Attendance not found']);
      } else {
        echo json_encode($row);
      }
    } else {
      $result = $conn->query("SELECT attendance_id, date, class_id FROM attendance");
      $all    = $result ? $result->fetch_all(MYSQLI_ASSOC) : [];
      echo json_encode($all);
    }
  } elseif ($method === 'POST') {
    // Required: date, class_id
    foreach (['date', 'class_id'] as $f) {
      if (empty($input[$f])) throw new Exception("Missing field: $f");
    }
    $stmt = $conn->prepare("
            INSERT INTO attendance (date, class_id)
            VALUES (?, ?)
        ");
    $stmt->bind_param(
      "si",
      $input['date'],
      $input['class_id']
    );
    $stmt->execute();
    http_response_code(201);
    echo json_encode([
      'success' => true,
      'message' => 'Attendance created',
      'attendance_id' => $conn->insert_id
    ]);
  } elseif ($method === 'PUT') {
    // Required: attendance_id
    if (empty($input['attendance_id'])) {
      throw new Exception("Missing attendance_id");
    }
    foreach (['date', 'class_id'] as $f) {
      if (!isset($input[$f])) throw new Exception("Missing field: $f");
    }
    $stmt = $conn->prepare("
            UPDATE attendance
               SET date = ?, class_id = ?
             WHERE attendance_id = ?
        ");
    $stmt->bind_param(
      "sii",
      $input['date'],
      $input['class_id'],
      $input['attendance_id']
    );
    $stmt->execute();
    echo json_encode(['success' => true, 'message' => 'Attendance updated']);
  } elseif ($method === 'DELETE') {
    if (empty($_GET['attendance_id'])) {
      throw new Exception("Missing attendance_id");
    }
    $aid = (int)$_GET['attendance_id'];

    // Begin transaction to cascade-delete attendance_student rows too
    $conn->begin_transaction();
    $stmt = $conn->prepare("DELETE FROM attendance_student WHERE attendance_id = ?");
    $stmt->bind_param("i", $aid);
    $stmt->execute();

    $stmt = $conn->prepare("DELETE FROM attendance WHERE attendance_id = ?");
    $stmt->bind_param("i", $aid);
    $stmt->execute();

    $conn->commit();
    echo json_encode(['success' => true, 'message' => 'Attendance deleted']);
  } else {
    http_response_code(405);
    echo json_encode(['error' => 'Method not allowed']);
  }
} catch (Exception $ex) {
  if ($conn->in_transaction) $conn->rollback();
  http_response_code(400);
  echo json_encode(['success' => false, 'error' => $ex->getMessage()]);
}
