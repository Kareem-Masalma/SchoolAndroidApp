<?php
// attendance_student.php

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle CORS preflight
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

require_once "db.inc.php";

// Read & decode JSON body for POST/PUT
$raw   = file_get_contents('php://input');
$input = json_decode($raw, true);
if (in_array($_SERVER['REQUEST_METHOD'], ['POST','PUT']) && json_last_error() !== JSON_ERROR_NONE) {
    http_response_code(400);
    echo json_encode(['success'=>false,'error'=>'Invalid JSON body']);
    exit;
}

$method = $_SERVER['REQUEST_METHOD'];

try {
    if ($method === 'GET') {
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
        // Required JSON fields
        foreach (['attendance_id','student_id','attended'] as $f) {
            if (!isset($input[$f])) {
                throw new Exception("Missing field: $f");
            }
        }
        $aid      = (int)$input['attendance_id'];
        $sid      = (int)$input['student_id'];
        $attended = $input['attended'] ? '1' : '0';
        $excuse   = $input['excuse'] ?? null;

        $stmt = $conn->prepare("
            INSERT INTO attendance_student
              (attendance_id, student_id, attended, excuse)
            VALUES (?, ?, ?, ?)
        ");
        $stmt->bind_param("iiss", $aid, $sid, $attended, $excuse);
        $stmt->execute();

        http_response_code(201);
        echo json_encode([
            'success' => true,
            'message' => 'Record created'
        ]);

    } elseif ($method === 'PUT') {
        // Required JSON fields
        foreach (['attendance_id','student_id','attended'] as $f) {
            if (!isset($input[$f])) {
                throw new Exception("Missing field: $f");
            }
        }
        $aid      = (int)$input['attendance_id'];
        $sid      = (int)$input['student_id'];
        $attended = $input['attended'] ? '1' : '0';
        $excuse   = $input['excuse'] ?? null;

        $stmt = $conn->prepare("
            UPDATE attendance_student
               SET attended = ?, excuse = ?
             WHERE attendance_id = ? AND student_id = ?
        ");
        $stmt->bind_param("siii", $attended, $excuse, $aid, $sid);
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