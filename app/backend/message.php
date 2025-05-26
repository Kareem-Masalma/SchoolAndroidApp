<?php
// message.php

require_once "db.inc.php";
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

$method = $_SERVER['REQUEST_METHOD'];
$input  = json_decode(file_get_contents('php://input'), true);

try {
    if ($method === 'GET') {

        $where = "";
        $params = [];
        $types = "";

        if (isset($_GET['from_user_id'])) {
            $where .= " AND m.from_user_id = ?";
            $params[] = $_GET['from_user_id'];
            $types .= "i";
        }
        if (isset($_GET['to_user_id'])) {
            $where .= " AND m.to_user_id = ?";
            $params[] = $_GET['to_user_id'];
            $types .= "i";
        }

        $sql = "
            SELECT
              m.message_id,
              m.from_user_id,
              sender.first_name AS from_first_name,
              sender.last_name AS from_last_name,
              m.to_user_id,
              recipient.first_name AS to_first_name,
              recipient.last_name AS to_last_name,
              m.title,
              m.content,
              m.sent_date
            FROM message m
            JOIN users sender ON sender.user_id = m.from_user_id
            JOIN users recipient ON recipient.user_id = m.to_user_id
            WHERE 1=1 $where
            ORDER BY m.sent_date DESC
        ";

        $stmt = $conn->prepare($sql);
        if (!empty($params)) {
            $stmt->bind_param($types, ...$params);
        }
        $stmt->execute();
        $result = $stmt->get_result();
        echo json_encode($result->fetch_all(MYSQLI_ASSOC));

    } elseif ($method === 'POST') {
        foreach (['from_user_id', 'to_user_id', 'title', 'content', 'sent_date'] as $key) {
            if (empty($input[$key])) {
                throw new Exception("Missing field: $key");
            }
        }

        $stmt = $conn->prepare(
            "INSERT INTO message (from_user_id, to_user_id, title, content, sent_date)
             VALUES (?, ?, ?, ?, ?)"
        );
        $stmt->bind_param(
            "iisss",
            $input['from_user_id'],
            $input['to_user_id'],
            $input['title'],
            $input['content'],
            $input['sent_date']
        );
        $stmt->execute();

        echo json_encode(['message_id' => $conn->insert_id]);

    } elseif ($method === 'DELETE') {
        if (!isset($_GET['message_id'])) {
            throw new Exception("Missing message_id");
        }
        $stmt = $conn->prepare("DELETE FROM message WHERE message_id = ?");
        $stmt->bind_param("i", $_GET['message_id']);
        $stmt->execute();

        echo json_encode(['deleted' => $stmt->affected_rows]);

    } else {
        http_response_code(405);
        echo json_encode(['error' => 'Method Not Allowed']);
    }
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(['error' => $e->getMessage()]);
}