package com.j1p3ter.userserver.presentation.response;

import java.time.LocalDateTime;

public record CommonApiResponse(int status, Object data, String error, LocalDateTime timeStamp) {
}