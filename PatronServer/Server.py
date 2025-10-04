import os.path
from http.server import HTTPServer, BaseHTTPRequestHandler

# Config
StaticRootFolder = "./WebRoot"
Host = "localhost"
Port = 1234
Logging = True
ContentTypeAdd = False

# Global Variable
FilesCache = {}
ErrorCache = {}
ErrorPageList = [400, 401, 403, 404, 500, 501, 502, 503, 520, 521, 523]


def GenerateIndexPage(FolderPath: str, WebFolderPath: str) -> bytes:
    if WebFolderPath == "/":
        WebFolderPath = ""
    WebFolder = WebFolderPath.split('/')[-1]
    if WebFolder == "":
        WebFolder = "Root"
    HtmlMain = f"<!DOCTYPE html>\n<html lang=\"zh_cn\">\n<head>\n<meta charset=\"UTF-8\">\n<title>{WebFolder}</title>\n</head>\n<body>\n"
    FileList = os.listdir(FolderPath)
    HtmlMain += f"<h1>{len(FileList)} Files in [{WebFolder}]</h1>"
    HtmlMain += "".join([f"<a href=\"{WebFolderPath}/{File}\">{File}</a><br>\n" for File in FileList])
    HtmlMain += f"</body>\n</html>"
    return HtmlMain.encode("utf-8")


def GetFiles(WebPath: str) -> tuple[bytes | None, str | None]:
    # if WebPath in FilesCache:
    #     return FilesCache[WebPath], None
    Path = StaticRootFolder + WebPath
    if not os.path.exists(Path):
        print(f"File not found: {Path}")
        return None, None
    if os.path.isdir(Path):
        return GenerateIndexPage(Path, WebPath), "html"
    with open(Path, 'rb') as Files:
        FilesRaw = Files.read()
    # FilesCache[WebPath] = FilesRaw
    return FilesRaw, None


def GetErrorPage(ErrorCode: int) -> bytes:
    if ErrorCode in ErrorCache:
        return ErrorCache[ErrorCode]
    if ErrorCode not in ErrorPageList:
        return b""
    with open(f"./ErrorPages/{ErrorCode}.html", 'rb') as ErrorPage:
        ErrorPageRaw = ErrorPage.read()
    ErrorCache[ErrorCode] = ErrorPageRaw
    return ErrorPageRaw


ContentTypesDictionary: dict[str, str] = {
    "html": "text/html",
    "js": "text/javascript",
    "css": "text/css",
    "png": "image/png",
    "jpg": "image/jpeg",
    "gif": "image/gif",
    "ico": "image/x-icon",
    "txt": "text/plain",
    "json": "application/json",
    "xml": "application/xml",
    "pdf": "application/pdf",
    "zip": "application/zip",
    "7z": "application/x-7z-compressed",
    "doc": "application/msword",
    "docx": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    "xls": "application/vnd.ms-excel",
    "xlsx": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    "ppt": "application/vnd.ms-powerpoint",
    "pptx": "application/vnd.openxmlformats-officedocument.presentationml.presentation",
    "mp3": "audio/mpeg",
    "wav": "audio/wav",
    "ogg": "audio/ogg",
    "mp4": "video/mp4",
    "avi": "video/x-msvideo",
    "mov": "video/quicktime",
    "flv": "video/x-flv",
    "swf": "application/x-shockwave-flash",
    "exe": "application/x-msdownload",
    "msi": "application/x-msdownload",
    "csv": "text/csv",
}


def GetContentType(FilesEXT: str) -> str:
    return ContentTypesDictionary.get(FilesEXT.lower(), "text/plain")


class StaticServerHandler(BaseHTTPRequestHandler):
    @staticmethod
    def Path_Split(Path: str) -> tuple[str, dict[str, str] | None]:
        # Return RawPath ArgsDictionary
        if '?' in Path:
            Path, Args = Path.split('?')
            Args = dict([i.split('=') for i in Args.split('&')])
            return Path, Args
        else:
            return Path, None

    def send_response(self, Code, Message=None):
        if Logging:
            self.log_request(Code)
        self.send_response_only(Code, Message)

    def do_GET(self):
        Path = self.path
        RawPath, Args = self.Path_Split(Path)
        FilesEXT = RawPath.split('.')[-1]
        FileRaw, NewEXT = GetFiles(RawPath)
        if NewEXT is not None:
            FilesEXT = NewEXT
        if FileRaw is None:
            ErrorCode = 404
            self.send_response(ErrorCode)
            self.end_headers()
            self.wfile.write(GetErrorPage(ErrorCode))
        else:
            self.send_response(200)
            if ContentTypeAdd:
                self.send_header('Content-type', GetContentType(FilesEXT))
            self.end_headers()
            self.wfile.write(FileRaw)


if __name__ == '__main__':
    Server_Address = (Host, Port)
    HttpServer = HTTPServer(Server_Address, StaticServerHandler)
    print(f"Server Is Running On {Host}:{Port}")
    HttpServer.serve_forever()
