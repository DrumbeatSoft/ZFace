package com.drumbeat.zface.config;

/**
 * @author ZuoHailong
 * @date 2020/5/29
 */
public class ZFaceConfig {

    /**
     * 模型文件在文件服务器所处位置的前置路径
     */
    private String resource_model_download_base_url;
    /**
     * so文件在文件服务器所处位置的前置路径
     */
    private String resource_so_download_base_url;

    public static Builder newBuilder() {
        return new Builder();
    }

    private ZFaceConfig(Builder builder) {
        this.resource_model_download_base_url = builder.resource_model_download_base_url;
        this.resource_so_download_base_url = builder.resource_so_download_base_url;
    }

    public String getResource_model_download_base_url() {
        return resource_model_download_base_url;
    }

    public String getResource_so_download_base_url() {
        return resource_so_download_base_url;
    }

    public final static class Builder {

        /**
         * 模型文件在文件服务器所处位置的前置路径
         */
        private String resource_model_download_base_url;
        /**
         * so文件在文件服务器所处位置的前置路径
         */
        private String resource_so_download_base_url;

        private Builder() {
        }

        /**
         * 机器学习模型文件在文件服务器上存储的 baseurl
         *
         * @param resource_model_download_base_url 机器学习模型文件 baseurl
         */
        public Builder setResource_model_download_base_url(String resource_model_download_base_url) {
            this.resource_model_download_base_url = resource_model_download_base_url;
            return this;
        }

        /**
         * so 文件在文件服务器上存储的 baseurl
         *
         * @param resource_so_download_base_url so 文件 baseurl
         */
        public Builder setResource_so_download_base_url(String resource_so_download_base_url) {
            this.resource_so_download_base_url = resource_so_download_base_url;
            return this;
        }

        /**
         * 构建 ZFaceConfig
         */
        public ZFaceConfig build() {
            return new ZFaceConfig(this);
        }
    }
}
