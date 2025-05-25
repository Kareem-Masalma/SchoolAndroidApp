<?php
// attendance.php

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

// Simple YYYY-MM-DD validation
function validateDate(string $d): bool {
    $dt = DateTime::createFromFormat('Y-m-d', $d);
    return $dt && $dt->format('Y-m-d') === $d;
}

$method = $_SERVER['REQUEST_METHOD'];
$inTxn  = false;

try {
    if ($method === 'GET') {
        if (isset($_GET['attendance_id'])) {
            $aid  = (int)$_GET['attendance_id'];
            $stmt = $conn->prepare("
                SELECT attendance_id, date, class_id
                  FROM attendance
                 WHERE attendance_id = ?
            "
            );
            $stmt->bind_param("i", $aid);
            $stmt->execute();
            $row = $stmt->get_result()->fetch_assoc();
            if (!$row) {
                http_response_code(404);
                echo json_encode(['error'=>'Attendance not found']);
            } else {
                echo json_encode($row);
            }
        } else {
            $result = $conn->query("SELECT attendance_id, date, class_id FROM attendance");
            $all    = $result ? $result->fetch_all(MYSQLI_ASSOC) : [];
            echo json_encode($all);
        }

    } elseif ($method === 'POST') {
        // Required JSON fields
        foreach (['date','class_id'] as $f) {
            if (!isset($input[$f])) {
                throw new Exception("Missing field: $f");
            }
        }
        $date    = $input['date'];
        $classId = (int)$input['class_id'];

        if (!validateDate($date)) {
            throw new Exception("Invalid date format, use YYYY-MM-DD");
        }

        $stmt = $conn->prepare("
            INSERT INTO attendance (date, class_id)
            VALUES (?, ?)
        "
        );
        $stmt->bind_param("si", $date, $classId);
        $stmt->execute();

        http_response_code(201);
        echo json_encode([
            'success'       => true,
            'message'       => 'Attendance created',
            'attendance_id' => $conn->insert_id
        ]);

    } elseif ($method === 'PUT') {
        // Required JSON fields
        foreach (['attendance_id','date','class_id'] as $f) {
            if (!isset($input[$f])) {
                throw new Exception("Missing field: $f");
            }
        }
        $aid     = (int)$input['attendance_id'];
        $date    = $input['date'];
        $classId = (int)$input['class_id'];

        if (!validateDate($date)) {
            throw new Exception("Invalid date format, use YYYY-MM-DD");
        }

        $stmt = $conn->prepare("UPDATE attendance SET date = ?, class_id = ? WHERE attendance_id = ?");
        $stmt->bind_param("sii", $date, $classId, $aid);
        $stmt->execute();

        echo json_encode(['success'=>true, 'message'=>'Attendance updated']);

    } elseif ($method === 'DELETE') {
        if (empty($_GET['attendance_id'])) {
            throw new Exception("Missing attendance_id");
        }
        $aid = (int)$_GET['attendance_id'];

        $conn->begin_transaction();
        $inTxn = true;

        $stmt = $conn->prepare("DELETE FROM attendance_student WHERE attendance_id = ?");
        $stmt->bind_param("i", $aid);
        $stmt->execute();

        $stmt = $conn->prepare("DELETE FROM attendance WHERE attendance_id = ?");
        $stmt->bind_param("i", $aid);
        $stmt->execute();

        $conn->commit();
        echo json_encode(['success'=>true, 'message'=>'Attendance deleted']);

    } else {
        http_response_code(405);
        echo json_encode(['error'=>'Method not allowed']);
    }

} catch (Exception $ex) {
    if ($inTxn) {
        $conn->rollback();
    }
    http_response_code(400);
    echo json_encode(['success'=>false,'error'=>$ex->getMessage()]);
}