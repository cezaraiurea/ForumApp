export function postAnswer(formData) {
    return request('/answers', {
        method: 'POST',
        body: formData,
    });
} 