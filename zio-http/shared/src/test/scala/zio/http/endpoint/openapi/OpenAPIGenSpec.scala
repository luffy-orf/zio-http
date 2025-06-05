test("text/plain response examples") {
  val endpoint = Endpoint(GET / "greeting" / string("name"))
    .outCodec(HttpCodec.content[String](MediaType.text.plain))
    .examplesOut("hello" -> "Hello, World!")

  val generated = OpenAPIGen.fromEndpoints("Text Plain Example", "1.0", endpoint)
  val json      = toJsonAst(generated)
  val expected  = """{
                   |  "openapi" : "3.1.0",
                   |  "info" : {
                   |    "title" : "Text Plain Example",
                   |    "version" : "1.0"
                   |  },
                   |  "paths" : {
                   |    "/greeting/{name}" : {
                   |      "get" : {
                   |        "parameters" : [
                   |          {
                   |            "name" : "name",
                   |            "in" : "path",
                   |            "required" : true,
                   |            "schema" : {
                   |              "type" : "string"
                   |            },
                   |            "style" : "simple"
                   |          }
                   |        ],
                   |        "responses" : {
                   |          "200" : {
                   |            "content" : {
                   |              "text/plain" : {
                   |                "schema" : {
                   |                  "type" : "string"
                   |                },
                   |                "examples" : {
                   |                  "hello" : {
                   |                    "value" : "Hello, World!"
                   |                  }
                   |                }
                   |              }
                   |            }
                   |          }
                   |        }
                   |      }
                   |    }
                   |  },
                   |  "components" : {}
                   |}""".stripMargin
  assertTrue(json == toJsonAst(expected))
}

test("media type specific examples") {
  val endpoint = Endpoint(GET / "examples")
    .outCodec(
      HttpCodec.content[String](MediaType.text.plain).examples("plain" -> "Hello, World!") |
        HttpCodec.content[String](MediaType.text.html).examples("html" -> "<p>Hello, World!</p>") |
        HttpCodec
          .content[Map[String, String]](MediaType.application.`x-www-form-urlencoded`)
          .examples("form" -> Map("name" -> "John", "age" -> "30")) |
        HttpCodec
          .content[Map[String, String]](MediaType.multipart.`form-data`)
          .examples("multipart" -> Map("file" -> "content", "type" -> "text/plain")),
    )

  val generated = OpenAPIGen.fromEndpoints("Media Type Examples", "1.0", endpoint)
  val json      = toJsonAst(generated)
  val expected  = """{
                   |  "openapi" : "3.1.0",
                   |  "info" : {
                   |    "title" : "Media Type Examples",
                   |    "version" : "1.0"
                   |  },
                   |  "paths" : {
                   |    "/examples" : {
                   |      "get" : {
                   |        "responses" : {
                   |          "200" : {
                   |            "content" : {
                   |              "text/plain" : {
                   |                "schema" : {
                   |                  "type" : "string"
                   |                },
                   |                "examples" : {
                   |                  "plain" : {
                   |                    "value" : "Hello, World!"
                   |                  }
                   |                }
                   |              },
                   |              "text/html" : {
                   |                "schema" : {
                   |                  "type" : "string"
                   |                },
                   |                "examples" : {
                   |                  "html" : {
                   |                    "value" : "<p>Hello, World!</p>"
                   |                  }
                   |                }
                   |              },
                   |              "application/x-www-form-urlencoded" : {
                   |                "schema" : {
                   |                  "type" : "object",
                   |                  "additionalProperties" : {
                   |                    "type" : "string"
                   |                  }
                   |                },
                   |                "examples" : {
                   |                  "form" : {
                   |                    "value" : "name=John&age=30"
                   |                  }
                   |                }
                   |              },
                   |              "multipart/form-data" : {
                   |                "schema" : {
                   |                  "type" : "object",
                   |                  "additionalProperties" : {
                   |                    "type" : "string"
                   |                  }
                   |                },
                   |                "examples" : {
                   |                  "multipart" : {
                   |                    "value" : {
                   |                      "file" : "content",
                   |                      "type" : "text/plain"
                   |                    }
                   |                  }
                   |                }
                   |              }
                   |            }
                   |          }
                   |        }
                   |      }
                   |    }
                   |  },
                   |  "components" : {}
                   |}""".stripMargin
  assertTrue(json == toJsonAst(expected))
}

test("error responses with different media types") {
  val endpoint = Endpoint(GET / "error-examples")
    .outCodec(
      HttpCodec.content[String](MediaType.text.plain).examples("error" -> "Not Found") |
        HttpCodec
          .content[Map[String, String]](MediaType.application.json)
          .examples("jsonError" -> Map("error" -> "Not Found", "code" -> "404")),
    )
    .errorCodec(
      HttpCodec.status(Status.NotFound) |
        HttpCodec.status(Status.InternalServerError),
    )

  val generated = OpenAPIGen.fromEndpoints("Error Examples", "1.0", endpoint)
  val json      = toJsonAst(generated)
  val expected  = """{
                   |  "openapi" : "3.1.0",
                   |  "info" : {
                   |    "title" : "Error Examples",
                   |    "version" : "1.0"
                   |  },
                   |  "paths" : {
                   |    "/error-examples" : {
                   |      "get" : {
                   |        "responses" : {
                   |          "200" : {
                   |            "content" : {
                   |              "text/plain" : {
                   |                "schema" : {
                   |                  "type" : "string"
                   |                },
                   |                "examples" : {
                   |                  "error" : {
                   |                    "value" : "Not Found"
                   |                  }
                   |                }
                   |              },
                   |              "application/json" : {
                   |                "schema" : {
                   |                  "type" : "object",
                   |                  "additionalProperties" : {
                   |                    "type" : "string"
                   |                  }
                   |                },
                   |                "examples" : {
                   |                  "jsonError" : {
                   |                    "value" : {
                   |                      "error" : "Not Found",
                   |                      "code" : "404"
                   |                    }
                   |                  }
                   |                }
                   |              }
                   |            }
                   |          },
                   |          "404" : {
                   |            "description" : "Not Found"
                   |          },
                   |          "500" : {
                   |            "description" : "Internal Server Error"
                   |          }
                   |        }
                   |      }
                   |    }
                   |  },
                   |  "components" : {}
                   |}""".stripMargin
  assertTrue(json == toJsonAst(expected))
}

test("nested form data and binary content") {
  val endpoint = Endpoint(POST / "complex-upload")
    .inCodec(
      HttpCodec
        .content[Map[String, Any]](MediaType.multipart.`form-data`)
        .examples(
          "nested" -> Map(
            "user"     -> Map("name" -> "John", "age" -> "30"),
            "file"     -> "binary content",
            "metadata" -> Map("type" -> "document", "size" -> "1024"),
          ),
        ),
    )
    .outCodec(
      HttpCodec
        .content[Array[Byte]](MediaType.application.`octet-stream`)
        .examples("binary" -> "SGVsbG8gV29ybGQ="), // Base64 encoded "Hello World"
    )

  val generated = OpenAPIGen.fromEndpoints("Complex Upload", "1.0", endpoint)
  val json      = toJsonAst(generated)
  val expected  = """{
                   |  "openapi" : "3.1.0",
                   |  "info" : {
                   |    "title" : "Complex Upload",
                   |    "version" : "1.0"
                   |  },
                   |  "paths" : {
                   |    "/complex-upload" : {
                   |      "post" : {
                   |        "requestBody" : {
                   |          "content" : {
                   |            "multipart/form-data" : {
                   |              "schema" : {
                   |                "type" : "object",
                   |                "additionalProperties" : true
                   |              },
                   |              "examples" : {
                   |                "nested" : {
                   |                  "value" : {
                   |                    "user" : {
                   |                      "name" : "John",
                   |                      "age" : "30"
                   |                    },
                   |                    "file" : "binary content",
                   |                    "metadata" : {
                   |                      "type" : "document",
                   |                      "size" : "1024"
                   |                    }
                   |                  }
                   |                }
                   |              }
                   |            }
                   |          },
                   |          "required" : true
                   |        },
                   |        "responses" : {
                   |          "200" : {
                   |            "content" : {
                   |              "application/octet-stream" : {
                   |                "schema" : {
                   |                  "type" : "string",
                   |                  "format" : "binary"
                   |                },
                   |                "examples" : {
                   |                  "binary" : {
                   |                    "value" : "SGVsbG8gV29ybGQ="
                   |                  }
                   |                }
                   |              }
                   |            }
                   |          }
                   |        }
                   |      }
                   |    }
                   |  },
                   |  "components" : {}
                   |}""".stripMargin
  assertTrue(json == toJsonAst(expected))
}

test("custom media type handling") {
  val customMediaType = MediaType.custom("application/vnd.company+json")
  val endpoint        = Endpoint(GET / "custom")
    .outCodec(
      HttpCodec
        .content[Map[String, Any]](customMediaType)
        .examples(
          "custom" -> Map(
            "data" -> Map(
              "id"         -> "123",
              "type"       -> "custom",
              "attributes" -> Map("name" -> "Custom Resource"),
            ),
          ),
        ),
    )

  val generated = OpenAPIGen.fromEndpoints("Custom Media Type", "1.0", endpoint)
  val json      = toJsonAst(generated)
  val expected  = """{
                   |  "openapi" : "3.1.0",
                   |  "info" : {
                   |    "title" : "Custom Media Type",
                   |    "version" : "1.0"
                   |  },
                   |  "paths" : {
                   |    "/custom" : {
                   |      "get" : {
                   |        "responses" : {
                   |          "200" : {
                   |            "content" : {
                   |              "application/vnd.company+json" : {
                   |                "schema" : {
                   |                  "type" : "object",
                   |                  "additionalProperties" : true
                   |                },
                   |                "examples" : {
                   |                  "custom" : {
                   |                    "value" : {
                   |                      "data" : {
                   |                        "id" : "123",
                   |                        "type" : "custom",
                   |                        "attributes" : {
                   |                          "name" : "Custom Resource"
                   |                        }
                   |                      }
                   |                    }
                   |                  }
                   |                }
                   |              }
                   |            }
                   |          }
                   |        }
                   |      }
                   |    }
                   |  },
                   |  "components" : {}
                   |}""".stripMargin
  assertTrue(json == toJsonAst(expected))
}
