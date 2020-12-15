<template>
    <div class="box">
        <vue-qr :text="data.url" :margin="0" colorDark="#000" colorLight="#fff" :logoSrc="data.icon + '?cache'" :logoScale="0.2" :size="200" :dotScale="1"></vue-qr>
    </div>
</template>

<script>
import vueQr from 'vue-qr';
export default {
    components: {
        vueQr
    },
    data() {
        return {
            data: {
                url: 'http://bilibili.com',
                icon: require('../../assets/img/chrome.png')
            }
        };
    },
    created() {},
    methods: {}
};
</script>

<style lang="less" scoped>
</style>
