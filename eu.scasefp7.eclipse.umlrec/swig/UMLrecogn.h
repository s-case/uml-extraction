#pragma once

#include <iostream>
#include <vector>
#include "cv.h"

struct wordBox;
struct _object;


class UMLrecog
{
public:
	UMLrecog();
	~UMLrecog();
	void clear();
	void setTessPath( std::string tessPath );
	void setParameters( bool isUseCase=false, bool showImages=false, int thresh=230, double sizeRate=1.0, double distNeightborObjects=20, double coverAreaThr=1.0 );
	bool setImage( std::string fileName );
	int process();
	std::vector<std::string> getXMIcontent();

private:
	void convert2AUTHformat();
	int system_hidden(const char *cmdArgs);
	void imageResizing( cv::Mat &img );
	bool check4Transparency( cv::Mat &img );
	bool textDiscrimination( cv::Mat input, cv::Mat &clearImg, std::map<wordBox,std::string> &OCRtext );
	bool objectDetection( cv::Mat &i_cleanImg, std::vector<_object> &i_objects, std::vector<_object> &i_arrows );
	bool objectRecongition_AD( cv::Mat inputImg, std::vector<_object> i_objects, std::vector<_object> i_arrows, std::map<wordBox,std::string> OCRtext );
	bool objectRecongition_UC( cv::Mat inputImg, std::vector<_object> i_objects, std::vector<_object> i_arrows, std::map<wordBox,std::string> OCRtext );

private:
	std::string m_fileName;
	std::string m_tessPath;
	std::vector<std::string> m_XMIcontent;
	cv::Mat m_img;
	// parameters
	bool m_isUseCase;
	bool m_showImages;
	int m_threshold;
	double m_sizeRate;
	double m_distNeightborObjects;
	double m_coverAreaThr;
};
