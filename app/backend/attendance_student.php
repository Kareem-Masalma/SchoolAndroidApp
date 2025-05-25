<?php
// attendance_student.php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

require_once "db.inc.php";

$method = $_SERVER['REQUEST_METHOD'];
$input  = json_decode(file_get_contents('php://input'), true);

try {
  if ($method === 'GET') {
    // List all or one record by composite PK
    if (isset($_GET['attendance_id'], $_GET['student_id'])) {
      $aid = (int)$_GET['attendance_id'];
      $sid = (int)$_GET['student_id'];
      $stmt = $conn->prepare("
                SELECT attendance_id, student_id, attended, excuse
                FROM attendance_student
                WHERE attendance_id = ? AND student_id = ?
            ");
      $stmt->bind_param("ii", $aid, $sid);
      $stmt->execute();
      $row = $stmt->get_result()->fetch_assoc();
      if (!$row) {
        http_response_code(404);
        echo json_encode(['error' => 'Record not found']);
      } else {
        echo json_encode($row);
      }
    } else {
      $result = $conn->query("
                SELECT attendance_id, student_id, attended, excuse
                FROM attendance_student
            ");
      $all = $result ? $result->fetch_all(MYSQLI_ASSOC) : [];
      echo json_encode($all);
    }
  } elseif ($method === 'POST') {
    // Required: attendance_id, student_id, attended
    foreach (['attendance_id', 'student_id', 'attended'] as $f) {
      if (!isset($input[$f])) throw new Exception("Missing field: $f");
    }
    $excuse = $input['excuse'] ?? null;
    $stmt = $conn->prepare("
            INSERT INTO attendance_student
              (attendance_id, student_id, attended, excuse)
            VALUES (?, ?, ?, ?)
        ");
    $stmt->bind_param(
      "iiss",
      $input['attendance_id'],
      $input['student_id'],
      $input['attended'] ? '1' : '0',
      $excuse
    );
    $stmt->execute();
    http_response_code(201);
    echo json_encode([
      'success' => true,
      'message' => 'Record created'
    ]);
  } elseif ($method === 'PUT') {
    // Composite key required
    foreach (['attendance_id', 'student_id', 'attended'] as $f) {
      if (!isset($input[$f])) throw new Exception("Missing field: $f");
    }
    $excuse = $input['excuse'] ?? null;
    $stmt = $conn->prepare("
            UPDATE attendance_student
               SET attended = ?, excuse = ?
             WHERE attendance_id = ? AND student_id = ?
        ");
    $stmt->bind_param(
      "siii",
      $input['attended'] ? '1' : '0',
      $excuse,
      $input['attendance_id'],
      $input['student_id']
    );
    $stmt->execute();
    echo json_encode(['success' => true, 'message' => 'Record updated']);
  } elseif ($method === 'DELETE') {
    if (!isset($_GET['attendance_id'], $_GET['student_id'])) {
      throw new Exception("Missing attendance_id or student_id");
    }
    $aid = (int)$_GET['attendance_id'];
    $sid = (int)$_GET['student_id'];
    $stmt = $conn->prepare("
            DELETE FROM attendance_student
            WHERE attendance_id = ? AND student_id = ?
        ");
    $stmt->bind_param("ii", $aid, $sid);
    $stmt->execute();
    echo json_encode(['success' => true, 'message' => 'Record deleted']);
  } else {
    http_response_code(405);
    echo json_encode(['error' => 'Method not allowed']);
  }
} catch (Exception $ex) {
  http_response_code(400);
  echo json_encode(['success' => false, 'error' => $ex->getMessage()]);
}
