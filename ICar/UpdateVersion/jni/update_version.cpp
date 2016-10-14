//#define __OS_LINUX_CPP_H__
//#include "platform_def.h"
#include <unistd.h>
#include <sys/time.h>
#include <termios.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <assert.h>
#include <time.h>
#include "stdarg.h"
#include <errno.h>
#include <android/log.h>
#define LOG_TAG "System.out.c"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

#define MAX_TRACE_LENGTH      (256)
#define AUDIO_CMD_PORT "/dev/ttyUSB3"
typedef unsigned int uint32;
#define HANDLE        uint32
#define TRUE          true
#define FALSE        false
HANDLE hCom;
int tty_fd2 = -1;

const char PORT_NAME_PREFIX[] = "/dev/ttyUSB";
static char log_trace[MAX_TRACE_LENGTH];
char g_image_path[50] = { 0 };
char g_dir_image[100] = { 0 };
char g_version[200] = { 0 };

void show_log(char *msg, ...) {
	va_list ap;

	va_start(ap, msg);
	vsnprintf(log_trace, MAX_TRACE_LENGTH, msg, ap);
	va_end(ap);

	printf("%s\r\n", log_trace);
}

#if 0
void prog_log(uint32 writesize,uint32 size,int clear)
{
	uint32 progress = (uint32)(writesize * 100)/ size;

	printf( "progress : %d%% finish\r", progress);
}

void qdl_msg_log(int msgtype,char *msg1,char * msg2)
{
}

void qdl_log(char *msg)
{
}
#endif

static int config_uart(int fd) {
	/*set UART configuration*/
	struct termios newtio;

	if (tcgetattr(fd, &newtio) != 0)
		return -1;

	cfmakeraw(&newtio);

	//newtio.c_cflag &= ~CIGNORE;
	/*set baudrate*/
	cfsetispeed(&newtio, B115200);
	cfsetospeed(&newtio, B115200);

	/*set char bit size*/
	newtio.c_cflag &= ~CSIZE;
	newtio.c_cflag |= CS8;

	/*set check sum*/
	//newtio.c_cflag &= ~PARENB;
	//newtio.c_iflag  &= ~INPCK;
	/*set stop bit*/
	newtio.c_cflag &= ~CSTOPB;
	newtio.c_cflag |= CLOCAL | CREAD;
	newtio.c_cflag &= ~(PARENB | PARODD);

	newtio.c_iflag &=
			~(INPCK | BRKINT | PARMRK | ISTRIP | INLCR | IGNCR | ICRNL);
	newtio.c_iflag |= IGNBRK;
	newtio.c_iflag &= ~(IXON | IXOFF | IXANY);
	//newtio.c_iflag |= (INPCK | ISTRIP);

	newtio.c_lflag = 0;
	newtio.c_oflag = 0;

	//newtio.c_lflag &= ~(ECHO | ECHONL |ICANON|ISIG|IEXTEN);
	//newtio.c_iflag |= (INPCK | ISTRIP);

	/*set wait time*/
	newtio.c_cc[VMIN] = 0;
	newtio.c_cc[VTIME] = 10;

	tcflush(fd, TCIFLUSH);
	tcflush(fd, TCOFLUSH);

	if (tcsetattr(fd, TCSANOW, &newtio) != 0)
		return -1;

	return 0;
}
int openport(int *com_port) {
	int tmp_port = 3;
	int retry = 5;
	char pc_comport[32];
	LOGI("OPEN PORT");
	if (hCom) {
		LOGI("in openport, but already opened!");
		return TRUE; /*already opened*/
	}
	LOGI("openport START");
	start_probe_port: memset(pc_comport, 0, sizeof(pc_comport));
	sprintf(pc_comport, "%s%d", PORT_NAME_PREFIX, tmp_port);
	LOGI("pc_comport %s%d", PORT_NAME_PREFIX, tmp_port);
	LOGI("pc_comport START");
	if (access(pc_comport, F_OK)) {
		LOGI("access start");

		tmp_port++;
		retry--;

		if (retry > 0)
			goto start_probe_port;
		else
			return FALSE;
	}
	hCom = (HANDLE) open(pc_comport, O_RDWR);

	if (hCom == (HANDLE) -1) {
		hCom = NULL;
		return FALSE;
	} else
		config_uart((int) hCom);

	*com_port = tmp_port;

	return TRUE;
}
//added by maxx set image path
int set_Image_path(const char *str_image_path) {
	//char image_path[50] = {0};
	memset(g_image_path, 0, 50);
	LOGI("jni str_image_path=%s", str_image_path);
	strcpy(g_image_path, str_image_path);
	LOGI("jni g_image_path0000=%s", g_image_path);
	if (g_image_path != NULL) {
		return 1;
	} else {
		return -1;
	}
}

void qdl_sleep(int millsec) {
	/*linux sleep function's unit is second*/
	int second = millsec / 1000;

	if (millsec % 1000)
		second += 1;

	sleep(second);
}
int tty_write_audio_cmd(char* cmd, unsigned int len) {

	int bytes;
	LOGI(">>>>%s\n", cmd);
	bytes = write(tty_fd2, cmd, len);
	LOGI("tty_write_audio_cmd wrote bytes %d\n", bytes);
	return bytes;
}
int closeport(HANDLE com_fd) {
	 LOGI("start close com port");

	if (!com_fd)
		return 1;

	close(com_fd);

	hCom = NULL;

	return 1;
}
int tty_open_audio_cmd_port(void) {
	/*open ttyUSB0*/
	tty_fd2 = open(AUDIO_CMD_PORT, O_RDWR);
	if (tty_fd2 < 0) {
		LOGI("open PCM cmd port(%s) failed\n", AUDIO_CMD_PORT);
		return -1;
	}
	LOGI("open PCM cmd port(%s) succeeded,handle=%d\n", AUDIO_CMD_PORT,
			tty_fd2);
	return 0;
}
void tty_close_audio_cmd_port(void) {
	close (tty_fd2);
}
int tty_read_audio_cmd_rsp(char* buff, unsigned int buff_size) {
	int bytes_read;
	memset(buff, 0, buff_size);
	bytes_read = read(tty_fd2, buff, buff_size);
	if (bytes_read > 0) {
		LOGI("<<<<%s\n", buff);
	}
	LOGI("tty_read_audio_cmd_rsp read bytes %d\n", bytes_read);
	return bytes_read;
}

int tty_config_audio_cmd_port(void) {
	struct termios newtio;
	LOGI("tty_config_audio_cmd_port\n");

	/*get the PCM data cmd port configuration*/
	if (tcgetattr(tty_fd2, &newtio) != 0) {
		printf("tcgetattr error\n");
		return -1;
	}

	/*raw mode*/
	cfmakeraw(&newtio);

	/*set baudrate*/
	cfsetispeed(&newtio, B115200);
	cfsetospeed(&newtio, B115200);

	/*set char bit size*/
	newtio.c_cflag &= ~CSIZE;
	newtio.c_cflag |= CS8;

	/*set check sum*/
	newtio.c_cflag &= ~PARENB;

	/*set stop bit*/
	newtio.c_cflag &= ~CSTOPB;
	newtio.c_cflag |= CLOCAL | CREAD;
	newtio.c_cflag &= ~(PARENB | PARODD);

	newtio.c_iflag &=
			~(INPCK | BRKINT | PARMRK | ISTRIP | INLCR | IGNCR | ICRNL);
	newtio.c_iflag |= IGNBRK;
	newtio.c_iflag &= ~(IXON | IXOFF | IXANY);
	newtio.c_oflag = 0;

	newtio.c_lflag = 0;

	/*set wait time*/
	newtio.c_cc[VMIN] = 1;
	newtio.c_cc[VTIME] = 0;

	/*flush i/o buff*/
	tcflush(tty_fd2, TCIFLUSH);
	tcflush(tty_fd2, TCOFLUSH);

	if (tcsetattr(tty_fd2, TCSANOW, &newtio) != 0) {
		printf("tcsetattr error\n");
		return -1;
	}

	return 0;
}
//get g_version
int setVersion() {
	char* cmd;
	int cmd_len;
	int bytes_written, bytes_read;
	int bytes = 0;
	char buff[20] = { 0 };
	char cmd_rsp[300] = { 0 };
	char cmd_rsp_version[200];
	char *p, *p1 = NULL;
#if 0
	if (access(AUDIO_CMD_PORT, F_OK)) {
		LOGI("access failed");
		return FALSE;
	}
#endif
	LOGI("tty_fd2:%d", tty_fd2);
	if (tty_fd2 != -1) {
		tty_close_audio_cmd_port();
	}
	//open port sucess
	LOGI("OPEN PORT");
	if (0 != tty_open_audio_cmd_port()) {
		return -1;
	}
	//config port
	LOGI("config PORT");
	//if (0 != tty_config_audio_cmd_port()) {
		//return -1;
	//}
#if 0
	/*****************************/
	cmd = "ATE0\r\n";
	cmd_len = strlen(cmd);
	LOGI("write ate0");
	bytes_written = tty_write_audio_cmd(cmd, cmd_len);
	if (bytes_written != cmd_len) {
		tty_close_audio_cmd_port();
		return -1;
	}
	LOGI("read ate0");
	while (1) {
		bytes_read = tty_read_audio_cmd_rsp(cmd_rsp, sizeof(cmd_rsp));
		LOGI("ATE0 cmd_rsp:%s", cmd_rsp);
		LOGI("ATE0 bytes_read:%d", bytes_read);
		if (bytes_read < 0) {
			LOGI("failed to read cmd response\n");
			tty_close_audio_cmd_port();
			return -1;
		}
		if (strstr(cmd_rsp, "\r\nOK\r\n")) {
			break;
		}
	}
	LOGI("---------------------------");
	//write AT
	//sleep(1000);
#endif
	LOGI("WRITE AT+SIMCOMAIT");
	cmd = "AT+SIMCOMATI\r\n";
	cmd_len = strlen(cmd);
	LOGI("cmd_len%d\n", cmd_len);
	bytes_written = tty_write_audio_cmd(cmd, cmd_len);
	if (bytes_written != cmd_len) {
		tty_close_audio_cmd_port();
		return -1;
	}
	//qdl_sleep(1000);
	while (1) {
		//read AT
		bytes_read = tty_read_audio_cmd_rsp(cmd_rsp, sizeof(cmd_rsp));
		//LOGI("read AT VERSION:%s", cmd_rsp);

		LOGI("read AT VERSION: bytes_read%d", bytes_read);
		if (bytes_read < 0) {
			LOGI("failed to read cmd response\n");
			tty_close_audio_cmd_port();
			return -1;
		}
		if (strstr(cmd_rsp, "Revision:")) {
			break;
		}
		if (strstr(cmd_rsp, "\r\nERROR\r\n")) {
			tty_close_audio_cmd_port();
			return -1;
		}

	}
    LOGI("cmd_rsp:%s",cmd_rsp);
	//memset(g_image_path, 0, sizeof(g_image_path));
	//g_version = NULL;
	if (cmd_rsp != NULL) {
		p = strstr((char*) cmd_rsp, "Revision:");
		LOGI("p = %s",p);
	}
	memset(cmd_rsp_version, 0, sizeof(cmd_rsp_version));
	LOGI("p  SIZE=%d",strlen(p));
	if (p != NULL) {
		p1 = strstr(p, "\n");
		memcpy(cmd_rsp_version, p + 9, p1-p-9);
	}

	memcpy(g_version, cmd_rsp_version, sizeof(cmd_rsp_version));
	LOGI("g_version:%s", g_version);

	LOGI("CLOSE PORT");
	tty_close_audio_cmd_port();
	return 1;
}
 int qdlCopyFile() {
	int timeout;
	char* cmd;
	int cmd_len;
	int bytes_written, bytes_read;
	int bytes = 0;
	char buff[20] = { 0 };
	char cmd_rsp[200];
	char cmd_rsp_file[1024] = {0};
	int i = 0;
	int j=0;
	FILE *fp;
	int file_length = 0;
	char file_path[100] = { 0 };
	int file_max_num = 0;
	int result = 0;
	char buffer[1000*1024] = { 0 };
	//char buffer[1024] = { 0 };		//maxx temp
	int flag = 0;
	int result_temp = 256;
	int copyfile_per_len = 256;
	char cmd_temp[100] = { 0 };
	/*open diag com port*/
	LOGI("ACESECC PORT");
	//memset(pc_comport, 0, sizeof(pc_comport));
	//LOGI("pc_comport %s%d", PORT_NAME_PREFIX, tmp_port);
	LOGI("tty_fd2:%d", tty_fd2);
	if (tty_fd2 != -1) {
		tty_close_audio_cmd_port();
	}
	if (access(AUDIO_CMD_PORT, F_OK)) {
		LOGI("access failed");
		return FALSE;
	}

	//open port sucess
	LOGI("OPEN PORT");
	if (0 != tty_open_audio_cmd_port()) {
		return -1;
	}
	//config port
	LOGI("config PORT");
	if (0 != tty_config_audio_cmd_port()) {
		return -1;
	}

	/*****************************/
	cmd = "ATE0\r\n";
	cmd_len = strlen(cmd);
	LOGI("write ate0");
	bytes_written = tty_write_audio_cmd(cmd, cmd_len);
	if (bytes_written != cmd_len) {
		tty_close_audio_cmd_port();
		return -1;
	}
	LOGI("read ate0");
	while (1) {
		bytes_read = tty_read_audio_cmd_rsp(cmd_rsp, sizeof(cmd_rsp));
		LOGI("ATE0 cmd_rsp:%s", cmd_rsp);
		if (bytes_read < 0) {
			LOGI("failed to read cmd response\n");
			tty_close_audio_cmd_port();
			return -1;
		}
		if (strstr(cmd_rsp, "OK")) {
			break;
		} else if (strstr(cmd_rsp, "\r\nERROR\r\n")) {
			tty_close_audio_cmd_port();
			return -1;

		}
	}
	//memcpy(g_image_path, "/data/data", 20);
	LOGI("g_image_path:%s", g_image_path);
	strcpy(g_dir_image, g_image_path);
	strcat(g_dir_image, "/delta_1_2.delta");
	LOGI("g_dir_image:%s", g_dir_image);
//if file exits,deleted file
	/*****************************************/
	cmd = "AT+FSDEL=\"delta_1_2.delta\"\r\n";
		cmd_len = strlen(cmd);
		LOGI("DELETE FILE AT+FSDEL");
		bytes_written = tty_write_audio_cmd(cmd, cmd_len);
		if (bytes_written != cmd_len) {
			tty_close_audio_cmd_port();
			return -1;
		}
		LOGI("read FSDEL");
		while (1) {
			bytes_read = tty_read_audio_cmd_rsp(cmd_rsp, sizeof(cmd_rsp));
			LOGI("FSDEL cmd_rsp:%s", cmd_rsp);
			if (bytes_read < 0) {
				LOGI("failed to read cmd response\n");
				tty_close_audio_cmd_port();
				return -1;
			}
			if (strstr(cmd_rsp, "OK")||(strstr(cmd_rsp, "ERROR"))) {
				LOGI("DELETE file");
				break;
			}
		}
	/*************************************************/

   /***************move file start**************/
	if ((fp = fopen(g_dir_image, "rb")) == NULL) {
		LOGI("open file  failed");
		//fclose(fp);
		tty_close_audio_cmd_port();
		return -1;
	}
	LOGI("open file  succed");
	fseek(fp, 0, SEEK_SET);
	fseek(fp, 0, SEEK_END);
	flag = 0;
	file_length = ftell(fp); //file size
	LOGI("file length000:%d", file_length);
	/*************move file start***************/
#if 0
	if (file_length % copyfile_per_len == 0) {
		file_max_num = file_length / copyfile_per_len;
	} else {
		file_max_num = file_length / copyfile_per_len + 1;
		flag = 1;
	}
	LOGI("file_max_num:%d,flag:%d", file_max_num,flag);
	LOGI("MOVE FILE START");
	fseek(fp, 0, SEEK_SET);

	for (i = 0; i < file_max_num; i++) {
		if ((flag == 1) && (i == (file_max_num - 1))) {
			result_temp = file_length - copyfile_per_len * i;
		}
		LOGI("move file size result_temp:%d", result_temp);
		fseek(fp, result_temp * i, SEEK_SET);
		//qdl_sleep(10);

		result = fread(buffer, 1, result_temp, fp); // 返回值是读取的内容数量//maxx temp
        LOGI("FILE fp=%d,errno=%d",fp,errno);
		if (result != result_temp) {
			LOGI("READ result_temp BTTE FAILED:%d,errno=%d",result,errno);

			fclose(fp);
			tty_close_audio_cmd_port();
			return -1;
		}
		LOGI("MOVE 1024 BYTE");
		LOGI("file_length:%d", file_length);

		sprintf(cmd_temp, "AT+CFTRANRX=\"c:/delta_1_2.delta\",%d,1\r\n",
				result_temp);
		cmd_len = strlen(cmd_temp);
		LOGI("AT+CFTRANRX%d\n", cmd_len);
		bytes_written = tty_write_audio_cmd(cmd_temp, cmd_len);
		if (bytes_written != cmd_len) {
			fclose(fp);
			tty_close_audio_cmd_port();
			return -1;
		}
		//qdl_sleep(100);
		while (1) {
		bytes_read = tty_read_audio_cmd_rsp(cmd_rsp, sizeof(cmd_rsp));
		LOGI("read at+cftran rsp:%s", cmd_rsp);
		if (bytes_read < 0) {
			LOGI("failed to read cftran response\n");
			fclose(fp);
			tty_close_audio_cmd_port();
			return -1;
			}
			if (strstr(cmd_rsp, ">")) {
				break;
			}

		}

		LOGI("WRITE at+cftran content");
		bytes_written = tty_write_audio_cmd(buffer, result_temp);

		LOGI("bytes_written:%d,result_temp:%d", bytes_written,result_temp);
		LOGI("i= :%d", i);
		if (bytes_written != result_temp) {
			LOGI("WIRTE content failed");
			tty_close_audio_cmd_port();
			return -1;
		}

		while (1) {
			LOGI("read content resp");
			bytes_read = tty_read_audio_cmd_rsp(cmd_rsp, sizeof(cmd_rsp));
			LOGI("read at+cftran rsp:%s", cmd_rsp);
			if (bytes_read < 0) {
				LOGI("failed to read cftran response\n");
				fclose(fp);
				tty_close_audio_cmd_port();
				return -1;
			}
			if (strstr(cmd_rsp, "OK")) {
				break;
			} else if (strstr(cmd_rsp, "\r\nERROR\r\n")) {
				tty_close_audio_cmd_port();
				return -1;

			}
		}

	}  //for
#endif
		fseek(fp, 0, SEEK_SET);
		result = fread(buffer, 1, file_length, fp);
		printf("FILE fp=%d,errno=%d\r\n", fp, errno);
		if (result != file_length) {
			printf("READ result_temp BTTE FAILED:%d,errno=%d", result, errno);

			fclose(fp);
			tty_close_audio_cmd_port();
			return -1;
		}
		printf("MOVE %d BYTE start\r\n",file_length);
		sprintf(cmd_temp, "AT+CFTRANRX=\"c:/delta_1_2.delta\",%d\r\n",
				file_length);
		cmd_len = strlen(cmd_temp);
		printf("AT+CFTRANRX:%d\r\n", cmd_len);
		bytes_written = tty_write_audio_cmd(cmd_temp, cmd_len);
		if (bytes_written != cmd_len) {
			fclose(fp);
			tty_close_audio_cmd_port();
			return -1;
		}
		//qdl_sleep(100);
		while (1) {
			bytes_read = tty_read_audio_cmd_rsp(cmd_rsp, sizeof(cmd_rsp));
			printf("read at+CFTRANRX rsp:%s\r\n", cmd_rsp);
			if (bytes_read < 0) {
				printf("failed to read cftran response\r\n");
				fclose(fp);
				tty_close_audio_cmd_port();
				return -1;
			}
			if (strstr(cmd_rsp, ">")) {
				break;
			}

		}
		printf("WRITE at+CFTRANRX content\r\n");
		bytes_written = tty_write_audio_cmd(buffer, file_length);

		printf("bytes_written:%d,result_temp:%d\r\n", bytes_written, file_length);
		if (bytes_written != file_length) {
			printf("WIRTE content failed\r\n");
			tty_close_audio_cmd_port();
			return -1;
		}

		while (1) {
			printf("read content resp\r\n");
			bytes_read = tty_read_audio_cmd_rsp(cmd_rsp, sizeof(cmd_rsp));
			printf("read at+cftran rsp:%s\r\n", cmd_rsp);
			if (bytes_read < 0) {
				printf("failed to read cftran response\r\n");
				fclose(fp);
				tty_close_audio_cmd_port();
				return -1;
			}
			if (strstr(cmd_rsp, "OK")) {
				break;
			} else if (strstr(cmd_rsp, "\r\nERROR\r\n")) {
				tty_close_audio_cmd_port();
				return -1;

			}
		}
	fclose(fp);
	/*********move file end*******************/

	return 1;
}
int qdl_sim6320_update() {
	int timeout;
	char* cmd;
	int cmd_len;
	int bytes_written, bytes_read;
	int bytes = 0;
	char buff[20] = { 0 };
	char cmd_rsp[200];
	LOGI("tty_fd2:%d", tty_fd2);
	if (tty_fd2 != -1) {
		tty_close_audio_cmd_port();
	}
	if (access(AUDIO_CMD_PORT, F_OK)) {
		LOGI("access failed");
		return FALSE;
	}

	//open port sucess
	LOGI("OPEN PORT");
	if (0 != tty_open_audio_cmd_port()) {
		return -1;
	}
	//config port
	LOGI("config PORT");
	if (0 != tty_config_audio_cmd_port()) {
		return -1;
	}

	/*****************************/
	cmd = "ATE0\r\n";
	cmd_len = strlen(cmd);
	LOGI("write ate0");
	bytes_written = tty_write_audio_cmd(cmd, cmd_len);
	if (bytes_written != cmd_len) {
		tty_close_audio_cmd_port();
		return -1;
	}
	LOGI("read ate0");
	while (1) {
		bytes_read = tty_read_audio_cmd_rsp(cmd_rsp, sizeof(cmd_rsp));
		LOGI("ATE0 cmd_rsp:%s", cmd_rsp);
		if (bytes_read < 0) {
			LOGI("failed to read cmd response\n");
			tty_close_audio_cmd_port();
			return -1;
		}
		if (strstr(cmd_rsp, "OK")) {
			break;
		} else if (strstr(cmd_rsp, "\r\nERROR\r\n")) {
			tty_close_audio_cmd_port();
			return -1;

		}
	}

	//file  exits
	LOGI("update version AT:AT+CDELTA");
	cmd = "AT+CDELTA=\"delta_1_2.delta\"\r\n";
	cmd_len = strlen(cmd);
	LOGI("cmd_len%d\n", cmd_len);
	bytes_written = tty_write_audio_cmd(cmd, cmd_len);
	if (bytes_written != cmd_len) {
		tty_close_audio_cmd_port();
		return -1;
	}
	//read AT
	bytes_read = tty_read_audio_cmd_rsp(cmd_rsp, sizeof(cmd_rsp));
	LOGI("read AT rsp:%s", cmd_rsp);
	if (bytes_read < 0) {
		LOGI("failed to read cmd response\n");
		tty_close_audio_cmd_port();
		return -1;
	}
	if (!strstr(cmd_rsp, "1")) {
		return -1;
	}
	//reset
	LOGI("RESET");
	cmd = "AT+CRESET\r\n";
	cmd_len = strlen(cmd);
	LOGI("RESET cmd_len%d\n", cmd_len);
	bytes_written = tty_write_audio_cmd(cmd, cmd_len);
	if (bytes_written != cmd_len) {
		tty_close_audio_cmd_port();
		return -1;
	}
	//read AT
	bytes_read = tty_read_audio_cmd_rsp(cmd_rsp, sizeof(cmd_rsp));
	LOGI("read RESET AT rsp:%s", cmd_rsp);
	if (bytes_read < 0) {
		LOGI("failed to read RESET cmd response\n");
		tty_close_audio_cmd_port();
		return -1;
	}
	return 1;
}
int qdlProcessing() {
	int result;
	int msgresult;
	int timeout = 60;
	int state;
	char StrBuff[50];
	long int time, time1;
	int ret = 0;

	LOGI("qdlProcessing start");

	//qdl_pre_download();

	ret = qdl_sim6320_update();

	//	qdl_post_download();

	LOGI("ret processing ret = %d", ret);

	return ret;
}

#ifndef JAVA_JNI
int main(int argc, char *argv[]) {
//maxx
	//Processing(0);
	qdlProcessing(0);

}

#else
#include <jni.h>
static jint qset_Image_path(JNIEnv *env, jobject thiz,jstring jimage_path_dir)
{
	// char const * image_path_dir = env->GetStringUTFChars(image_path_dir, NULL);

	char const *image_path_dir = env->GetStringUTFChars(jimage_path_dir, NULL);
	//log_message("qlog_start(<%s>, <%s>) called!", image_path_dir);
	return set_Image_path((const char *)image_path_dir);
}

static jint gCopyFile()
{
	return qdlCopyFile();
}

static jint gProcessing()
{
	//char const * str_log_dir = env->GetStringUTFChars(image_path_dir, NULL);

	//log_message("qlog_start(<%s>, <%s>) called!", image_path_dir);

	return qdlProcessing();
}
static jstring qgetVersion(JNIEnv *env, jobject thiz)
{
	setVersion();
	//char* tmpstr = "return string succeeded";
	jstring rtstr = env->NewStringUTF(g_version);
	return rtstr;

}
static const char *classPathName = "com/simcom/updateversion/UpdateVersion";

static JNINativeMethod methods[] = {
	{	"qset_Image_path", "(Ljava/lang/String;)I", (void*)qset_Image_path},
	{	"gProcessing", "()I", (void*)gProcessing},
	{	"qgetVersion", "()Ljava/lang/String;", (void*)qgetVersion},
	{	"gCopyFile", "()I", (void*)gCopyFile},
	// {"qtty_get_version", "()I", (void*)qtty_get_version },
};

/*
 * Register several native methods for one class.
 */
static int registerNativeMethods(JNIEnv* env, const char* className,
		JNINativeMethod* gMethods, int numMethods)
{
	jclass clazz;

	clazz = env->FindClass(className);
	if (clazz == NULL) {
		fprintf(stderr, "Native registration unable to find class '%s'", className);
		return JNI_FALSE;
	}
	if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
		fprintf(stderr, "RegisterNatives failed for '%s'", className);
		return JNI_FALSE;
	}

	return JNI_TRUE;
}

/*
 * Register native methods for all classes we know about.
 */
static int registerNatives(JNIEnv* env)
{
	if (!registerNativeMethods(env, classPathName,
					methods, sizeof(methods) / sizeof(methods[0]))) {
		return JNI_FALSE;
	}

	return JNI_TRUE;
}

/*
 * Set some test stuff up.
 *
 * Returns the JNI version on success, -1 on failure.
 */

typedef union {
	JNIEnv* env;
	void* venv;
}UnionJNIEnvToVoid;

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	UnionJNIEnvToVoid uenv;
	uenv.venv = NULL;
	jint result = -1;
	JNIEnv* env = NULL;

	printf("JNI_OnLoad");

	if (vm->GetEnv(&uenv.venv, JNI_VERSION_1_4) != JNI_OK) {
		fprintf(stderr, "GetEnv failed");
		goto bail;
	}
	env = uenv.env;

	if (!registerNatives(env)) {
		fprintf(stderr, "registerNatives failed");
	}

	result = JNI_VERSION_1_4;

	bail:
	return result;
}
#endif

